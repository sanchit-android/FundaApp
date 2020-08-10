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

    private void parseJSONResponse(JSONObject jo, MFPriceModel model) throws java.text.ParseException {
        if (model.getPriceMap() == null) {
            model.setPriceMap(new HashMap<>());
        }
        Map<String, BigDecimal> navMap = model.getPriceMap();
        for (Map.Entry<String, Constants.DurationData> entry : PRICE_MAP.entrySet()) {
            Constants.DurationData durationData = entry.getValue();
            String key = entry.getKey();

            if (Constants.DurationBasis.IndexBased.equals(durationData.getDurationBasis())) {
                BigDecimal price = parseIndexBasedNAV(jo, durationData);
                navMap.put(entry.getKey(), price);
            } else if (Constants.DurationBasis.DurationBased.equals(durationData.getDurationBasis())) {
                parseDurationBasedNAV(jo, durationData, key, navMap);
            }
        }
    }

    private void parseDurationBasedNAV(JSONObject jo, Constants.DurationData durationData, String key, Map<String, BigDecimal> navMap) throws java.text.ParseException {
        Calendar todayMinusXDays = DateUtils.customDate(durationData.getDurationType(), durationData.getDuration());
        JSONArray data = (JSONArray) jo.get("data");
        Iterator itr = data.iterator();

        BigDecimal highPrice = null;
        BigDecimal lowPrice = null;
        BigDecimal price = null;
        while (itr.hasNext()) {
            JSONObject x = (JSONObject) itr.next();
            BigDecimal nav = new BigDecimal(x.get("nav").toString());
            Calendar cal = DateUtils.parseCal(x.get("date").toString(), "dd-MM-yyyy");
            if (cal.before(todayMinusXDays)) {
                break;
            }

            price = nav;

            if (highPrice == null || nav.compareTo(highPrice) > 0) {
                highPrice = nav;
            }
            if (lowPrice == null || nav.compareTo(lowPrice) < 0) {
                lowPrice = nav;
            }
        }

        if (durationData.isHighLowKeyAvailable()) {
            navMap.put(durationData.getHighKey(), highPrice);
            navMap.put(durationData.getLowKey(), lowPrice);
        }
        navMap.put(key, price);
    }

    private BigDecimal parseIndexBasedNAV(JSONObject jsonObject, Constants.DurationData x) {
        JSONArray data = (JSONArray) jsonObject.get("data");
        if (data != null && data.size() > x.getIndex()) {
            BigDecimal latestNAV = new BigDecimal(((JSONObject) data.get(x.getIndex())).get("nav").toString());
            return latestNAV;
        }
        return Constants.EMPTY_PRICE;
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
}
