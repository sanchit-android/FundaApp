package com.sanchit.funda.model.homesummary;

import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.model.MFPosition;
import com.sanchit.funda.model.MFPriceModel;
import com.sanchit.funda.model.MFTrade;
import com.sanchit.funda.utils.Constants;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopGrowthModel extends AbstractHomeSummary1Model {

    private static final int SCALE_DISPLAY = 2;

    private final KeyProvider keyProvider;
    private final boolean inverse;
    private final List<MFPosition> positions;
    private final List<MFTrade> trades;
    protected Comparator<BigDecimal> INCREASING_COMPARATOR = ((x, y) -> x.compareTo(y));
    protected Comparator<BigDecimal> DECREASING_COMPARATOR = ((x, y) -> y.compareTo(x));
    private String baseDuration;

    public TopGrowthModel(String header, KeyProvider keyProvider, boolean inverse, List<MFPosition> positions, List<MFTrade> trades,
                          String baseDuration) {
        super(header);
        this.keyProvider = keyProvider;
        this.inverse = inverse;
        this.positions = positions;
        this.trades = trades;
        this.baseDuration = baseDuration;

        initModel();
    }

    public TopGrowthModel(String header, KeyProvider keyProvider, List<MFPosition> positions, List<MFTrade> trades,
                          String baseDuration) {
        this(header, keyProvider, false, positions, trades, baseDuration);
    }

    public TopGrowthModel(String header, KeyProvider keyProvider, List<MFPosition> positions, List<MFTrade> trades) {
        this(header, keyProvider, false, positions, trades, Constants.Duration.T_1Y);
    }

    public TopGrowthModel(String header, KeyProvider keyProvider, boolean inverse, List<MFPosition> positions, List<MFTrade> trades) {
        this(header, keyProvider, inverse, positions, trades, Constants.Duration.T_1Y);
    }

    protected void initModel() {
        Map<String, BigDecimal> data = new HashMap<>();

        Map<String, BigDecimal> valuationMap = new HashMap<>();
        Map<String, BigDecimal> investedMap = new HashMap<>();

        CacheManager.Cache<String, MFPriceModel> priceCache = CacheManager.get(Caches.PRICES_BY_AMFI_ID, MFPriceModel.class);

        for (MFPosition position : positions) {
            String key = keyProvider.fetchKey(position.getFund());
            MFPriceModel prices = priceCache.get(position.getFund().getAmfiID());

            if (!investedMap.containsKey(key)) {
                investedMap.put(key, new BigDecimal(0));
            }
            BigDecimal invested = investedMap.get(key).add(position.getQuantity().multiply(prices.getPrice(baseDuration)).setScale(SCALE_DISPLAY, BigDecimal.ROUND_HALF_DOWN));
            investedMap.put(key, invested);


            if (!valuationMap.containsKey(key)) {
                valuationMap.put(key, new BigDecimal(0));
            }
            BigDecimal valuation = valuationMap.get(key).add(position.getQuantity().multiply(prices.getPrice(Constants.Duration.T)).setScale(SCALE_DISPLAY, BigDecimal.ROUND_HALF_DOWN));
            valuationMap.put(key, valuation);
        }

        for (MFTrade trade : trades) {
            String key = keyProvider.fetchKey(trade.getFund());
            MFPriceModel prices = priceCache.get(trade.getFund().getAmfiID());

            if (!investedMap.containsKey(key)) {
                investedMap.put(key, new BigDecimal(0));
            }
            BigDecimal invested = investedMap.get(key).add(trade.getQuantity().multiply(prices.getPrice(baseDuration)).setScale(SCALE_DISPLAY, BigDecimal.ROUND_HALF_DOWN));
            investedMap.put(key, invested);


            if (!valuationMap.containsKey(key)) {
                valuationMap.put(key, new BigDecimal(0));
            }
            BigDecimal valuation = valuationMap.get(key).add(trade.getQuantity().multiply(prices.getPrice(Constants.Duration.T)).setScale(SCALE_DISPLAY, BigDecimal.ROUND_HALF_DOWN));
            valuationMap.put(key, valuation);
        }

        String topDataLabel = null;
        BigDecimal topData = null;
        for (Map.Entry<String, BigDecimal> x : investedMap.entrySet()) {
            BigDecimal investment = x.getValue();
            BigDecimal valuation = valuationMap.get(x.getKey());

            BigDecimal profit = (valuation.subtract(investment)).divide(investment, SCALE_DISPLAY, BigDecimal.ROUND_HALF_UP);
            if (topData == null || getComparator().compare(profit, topData) > 0) {
                topData = profit;
                topDataLabel = x.getKey();
            }
        }

        content = topDataLabel;
    }

    private Comparator<BigDecimal> getComparator() {
        return inverse ? DECREASING_COMPARATOR : INCREASING_COMPARATOR;
    }

}
