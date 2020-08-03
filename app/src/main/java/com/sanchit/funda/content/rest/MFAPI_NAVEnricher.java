package com.sanchit.funda.content.rest;

import com.sanchit.funda.async.MFAPI_NAVAsyncLoader;
import com.sanchit.funda.model.MFPriceModel;
import com.sanchit.funda.utils.Constants;
import com.sanchit.funda.utils.DateUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.sanchit.funda.utils.Constants.PRICE_MAP;

public class MFAPI_NAVEnricher extends AbstractRestEnricher<String, MFPriceModel> {


    private static final Map<String, Map<MFAPI_NAVAsyncLoader.EnrichmentModel, MFPriceModel>> priceCache = new HashMap<>();
    private static final String mfAPI = "https://api.mfapi.in/mf/";
    private final MFAPI_NAVAsyncLoader.EnrichmentModel enrichmentModel;

    public MFAPI_NAVEnricher(MFAPI_NAVAsyncLoader.EnrichmentModel enrichmentModel) {
        super();
        this.enrichmentModel = enrichmentModel;
    }

    @Override
    public MFPriceModel enrich(String input) {
        if (priceCache.containsKey(input) && priceCache.get(input).containsKey(enrichmentModel)) {
            return priceCache.get(input).get(enrichmentModel);
        }

        MFPriceModel model = new MFPriceModel(input);
        try {
            JSONObject jo = callEndpoint(mfAPI + input);
            if (MFAPI_NAVAsyncLoader.EnrichmentModel.Default.equals(enrichmentModel)) {
                parseJSONResponse(jo, model);
            } else if (MFAPI_NAVAsyncLoader.EnrichmentModel.HL_52W.equals(enrichmentModel)) {
                parseJSONResponseFor52W_HL(jo, model);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }

        if (!priceCache.containsKey(input)) {
            priceCache.put(input, new HashMap<>());
        }
        priceCache.get(input).put(enrichmentModel, model);
        return model;
    }

    private void parseJSONResponse(JSONObject jo, MFPriceModel model) {
        if (model.getPriceMap() == null) {
            model.setPriceMap(new HashMap<>());
        }
        Map<String, BigDecimal> navMap = model.getPriceMap();
        for (Map.Entry<String, Integer> entry : PRICE_MAP.entrySet()) {
            BigDecimal price = parseTMinusXNAV(jo, entry.getValue());
            navMap.put(entry.getKey(), price);
        }
    }

    private void parseJSONResponseFor52W_HL(JSONObject jo, MFPriceModel model) throws java.text.ParseException {
        if (model.getPriceMap() == null) {
            model.setPriceMap(new HashMap<>());
        }
        Map<String, BigDecimal> navMap = model.getPriceMap();

        // Latest Price
        BigDecimal price = parseTMinusXNAV(jo, 1);
        navMap.put(Constants.Duration.T, price);

        // 52W Old Price
        Calendar todayMinus52W = DateUtils.customDate(Calendar.YEAR, -1);
        JSONArray data = (JSONArray) jo.get("data");
        Iterator itr = data.iterator();

        BigDecimal highPrice = null;
        BigDecimal lowPrice = null;
        while (itr.hasNext()) {
            JSONObject x = (JSONObject) itr.next();
            BigDecimal nav = new BigDecimal(x.get("nav").toString());
            Calendar cal = DateUtils.parseCal(x.get("date").toString(), "dd-MM-yyyy");
            if (cal.before(todayMinus52W)) {
                break;
            }

            if (highPrice == null || nav.compareTo(highPrice) > 0) {
                highPrice = nav;
            }
            if (lowPrice == null || nav.compareTo(lowPrice) < 0) {
                lowPrice = nav;
            }
        }
        navMap.put(Constants.Duration.High52W, highPrice);
        navMap.put(Constants.Duration.Low52W, lowPrice);
    }

    private JSONObject callEndpoint(String apiURL) throws IOException, ParseException {
        URL url = new URL(apiURL);
        URLConnection connection = url.openConnection();

        InputStream is = connection.getInputStream();
        InputStreamReader reader = new InputStreamReader(is);
        Object obj = new JSONParser().parse(reader);

        // typecasting obj to JSONObject
        return (JSONObject) obj;
    }


    private BigDecimal parseTMinusXNAV(JSONObject jsonObject, int x) {
        JSONArray data = (JSONArray) jsonObject.get("data");
        if (data != null && data.size() >= x) {
            BigDecimal latestNAV = new BigDecimal(((JSONObject) data.get(x - 1)).get("nav").toString());
            return latestNAV;
        }
        return Constants.EMPTY_PRICE;
    }

}
