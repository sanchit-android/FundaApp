package com.sanchit.funda.content.rest;

import com.sanchit.funda.model.MFPriceModel;
import com.sanchit.funda.utils.Constants;

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
import java.util.HashMap;
import java.util.Map;

import static com.sanchit.funda.utils.Constants.PRICE_MAP;

public class MFAPI_NAVEnricher extends AbstractRestEnricher<String, MFPriceModel> {

    private static final Map<String, MFPriceModel> priceCache = new HashMap<>();
    private String mfAPI = "https://api.mfapi.in/mf/";

    @Override
    public MFPriceModel enrich(String input) {
        if (priceCache.containsKey(input)) {
            return priceCache.get(input);
        }

        MFPriceModel model = new MFPriceModel(input);
        try {
            JSONObject jo = callEndpoint(mfAPI + input);
            parseJSONResponse(jo, model);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        priceCache.put(input, model);
        return model;
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

    private void parseJSONResponse(JSONObject jo, MFPriceModel model) {
        Map<String, BigDecimal> navMap = new HashMap<>();
        model.setPriceMap(navMap);
        for (Map.Entry<String, Integer> entry : PRICE_MAP.entrySet()) {
            BigDecimal price = parseTMinusXNAV(jo, entry.getValue());
            navMap.put(entry.getKey(), price);
        }
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
