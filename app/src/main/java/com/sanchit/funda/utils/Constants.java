package com.sanchit.funda.utils;

import android.content.Context;

import com.sanchit.funda.R;
import com.sanchit.funda.model.MFDetailModel;
import com.sanchit.funda.model.PositionViewModel;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static final BigDecimal EMPTY_PRICE = new BigDecimal(-1);

    public static final Map<String, DurationData> PRICE_MAP = new HashMap<>();

    private static final Map<Integer, Map<String, Comparator<PositionViewModel>>> POSITIONS_VIEW_COMPARATOR_MAP = new HashMap<>();
    private static final Map<Integer, Integer> POSITIONS_VIEW_SORT_ICON_MAP = new HashMap<>();

    private static final Map<String, Comparator<MFDetailModel>> POSITIONS_DETAIL_VIEW_COMPARATOR_MAP = new HashMap<>();

    static {
        PRICE_MAP.put(Duration.T, new DurationData(DurationBasis.IndexBased, 0));
        PRICE_MAP.put(Duration.T_1d, new DurationData(DurationBasis.IndexBased, 1));
        PRICE_MAP.put(Duration.T_10d, new DurationData(DurationBasis.DurationBased, Calendar.DATE, -10));
        PRICE_MAP.put(Duration.T_1M, new DurationData(DurationBasis.DurationBased, Calendar.MONTH, -1));
        PRICE_MAP.put(Duration.T_3M, new DurationData(DurationBasis.DurationBased, Calendar.MONTH, -3));
        PRICE_MAP.put(Duration.T_6M, new DurationData(DurationBasis.DurationBased, Calendar.MONTH, -6));
        PRICE_MAP.put(Duration.T_1Y, new DurationData(DurationBasis.DurationBased, Calendar.YEAR, -1, Duration.T_1YHigh, Duration.T_1YLow));
        PRICE_MAP.put(Duration.T_2Y, new DurationData(DurationBasis.DurationBased, Calendar.YEAR, -2));
        PRICE_MAP.put(Duration.T_3Y, new DurationData(DurationBasis.DurationBased, Calendar.YEAR, -3));
        PRICE_MAP.put(Duration.T_5Y, new DurationData(DurationBasis.DurationBased, Calendar.YEAR, -5));

        POSITIONS_VIEW_COMPARATOR_MAP.put(R.id.positions_view_header_fund, newMap((o1, o2) -> o1.getHead().compareTo(o2.getHead()), (o1, o2) -> o2.getHead().compareTo(o1.getHead())));
        POSITIONS_VIEW_COMPARATOR_MAP.put(R.id.positions_view_header_cost, newMap((o1, o2) -> o1.getInvestment().compareTo(o2.getInvestment()), (o1, o2) -> o2.getInvestment().compareTo(o1.getInvestment())));
        POSITIONS_VIEW_COMPARATOR_MAP.put(R.id.positions_view_header_valuation, newMap((o1, o2) -> o1.getValuation().compareTo(o2.getValuation()), (o1, o2) -> o2.getValuation().compareTo(o1.getValuation())));
        POSITIONS_VIEW_COMPARATOR_MAP.put(R.id.positions_view_header_daypnl, newMap((o1, o2) -> o1.getPnlDay().compareTo(o2.getPnlDay()), (o1, o2) -> o2.getPnlDay().compareTo(o1.getPnlDay())));
        POSITIONS_VIEW_COMPARATOR_MAP.put(R.id.positions_view_header_overallpnl, newMap((o1, o2) -> o1.getPnlOverall().compareTo(o2.getPnlOverall()), (o1, o2) -> o2.getPnlOverall().compareTo(o1.getPnlOverall())));
        POSITIONS_VIEW_COMPARATOR_MAP.put(R.id.positions_view_header_fund_category, newMap((o1, o2) -> o1.getFundCategory().compareTo(o2.getFundCategory()), (o1, o2) -> o2.getFundCategory().compareTo(o1.getFundCategory())));

        POSITIONS_VIEW_SORT_ICON_MAP.put(R.id.positions_view_header_fund, R.id.positions_view_header_fund_sort_icon);
        POSITIONS_VIEW_SORT_ICON_MAP.put(R.id.positions_view_header_valuation, R.id.positions_view_header_valuation_sort_icon);
        POSITIONS_VIEW_SORT_ICON_MAP.put(R.id.positions_view_header_cost, R.id.positions_view_header_cost_sort_icon);
        POSITIONS_VIEW_SORT_ICON_MAP.put(R.id.positions_view_header_daypnl, R.id.positions_view_header_daypnl_sort_icon);
        POSITIONS_VIEW_SORT_ICON_MAP.put(R.id.positions_view_header_overallpnl, R.id.positions_view_header_overallpnl_sort_icon);
        POSITIONS_VIEW_SORT_ICON_MAP.put(R.id.positions_view_header_fund_category, R.id.positions_view_header_fund_category_sort_icon);
    }

    public static void initData(Context context) {
        POSITIONS_DETAIL_VIEW_COMPARATOR_MAP.put(context.getResources().getStringArray(R.array.sort_options)[0], (o1, o2) -> o2.getPriceModel().get1MonthReturnComparable().compareTo(o1.getPriceModel().get1MonthReturnComparable()));
        //POSITIONS_DETAIL_VIEW_COMPARATOR_MAP.put(context.getResources().getStringArray(R.array.sort_options)[1], (o1, o2) -> o1.getPriceModel().get3MonthsReturnComparable().compareTo(o2.getPriceModel().get3MonthsReturnComparable()));
        POSITIONS_DETAIL_VIEW_COMPARATOR_MAP.put(context.getResources().getStringArray(R.array.sort_options)[1], (o1, o2) -> o2.getPriceModel().get3MonthsReturnComparable().compareTo(o1.getPriceModel().get3MonthsReturnComparable()));
        //POSITIONS_DETAIL_VIEW_COMPARATOR_MAP.put(context.getResources().getStringArray(R.array.sort_options)[3], (o1, o2) -> o1.getPriceModel().get6MonthsReturnComparable().compareTo(o2.getPriceModel().get6MonthsReturnComparable()));
        POSITIONS_DETAIL_VIEW_COMPARATOR_MAP.put(context.getResources().getStringArray(R.array.sort_options)[2], (o1, o2) -> o2.getPriceModel().get6MonthsReturnComparable().compareTo(o1.getPriceModel().get6MonthsReturnComparable()));
        //POSITIONS_DETAIL_VIEW_COMPARATOR_MAP.put(context.getResources().getStringArray(R.array.sort_options)[5], (o1, o2) -> o1.getPriceModel().get1YearReturnComparable().compareTo(o2.getPriceModel().get1YearReturnComparable()));
        POSITIONS_DETAIL_VIEW_COMPARATOR_MAP.put(context.getResources().getStringArray(R.array.sort_options)[3], (o1, o2) -> o2.getPriceModel().get1YearReturnComparable().compareTo(o1.getPriceModel().get1YearReturnComparable()));
        POSITIONS_DETAIL_VIEW_COMPARATOR_MAP.put(context.getResources().getStringArray(R.array.sort_options)[4], (o1, o2) -> o2.getPriceModel().get2YearReturnComparable().compareTo(o1.getPriceModel().get2YearReturnComparable()));
        POSITIONS_DETAIL_VIEW_COMPARATOR_MAP.put(context.getResources().getStringArray(R.array.sort_options)[5], (o1, o2) -> o2.getPriceModel().get3YearReturnComparable().compareTo(o1.getPriceModel().get3YearReturnComparable()));
        POSITIONS_DETAIL_VIEW_COMPARATOR_MAP.put(context.getResources().getStringArray(R.array.sort_options)[6], (o1, o2) -> o2.getPriceModel().get5YearReturnComparable().compareTo(o1.getPriceModel().get5YearReturnComparable()));
    }

    private static final Map<String, Comparator<PositionViewModel>> newMap(Comparator<PositionViewModel> c1, Comparator<PositionViewModel> c2) {
        Map<String, Comparator<PositionViewModel>> map = new HashMap<>();
        map.put(SortType.Ascending, c1);
        map.put(SortType.Descending, c2);
        return map;
    }

    public static final Comparator<PositionViewModel> getComparator(Integer viewId, String sortType) {
        return POSITIONS_VIEW_COMPARATOR_MAP.get(viewId).get(sortType);
    }

    public static Comparator<MFDetailModel> getPositionDetailComparator(String key) {
        return POSITIONS_DETAIL_VIEW_COMPARATOR_MAP.get(key);
    }

    public static final Integer getSortImage(Integer viewId) {
        return POSITIONS_VIEW_SORT_ICON_MAP.get(viewId);
    }

    public static final Collection<Integer> getAllSortImages() {
        return POSITIONS_VIEW_SORT_ICON_MAP.values();
    }

    public enum DurationBasis {
        IndexBased, DurationBased;
    }

    public interface SortType {
        String Ascending = "A";
        String Descending = "D";
    }

    public interface Side {
        String BUY = "Buy";
        String SELL = "Sell";
    }

    public interface Duration {
        String T = "T";
        String T_1d = "T_1d";
        String T_10d = "T_10d";
        String T_1M = "T_1M";
        String T_3M = "T_3M";
        String T_6M = "T_6M";
        String T_1Y = "T_1Y";
        String T_2Y = "T_2Y";
        String T_3Y = "T_3Y";
        String T_5Y = "T_5Y";

        String T_1YHigh = "T_1YHigh";
        String T_1YLow = "T_1YLow";
    }

    public static class DurationData {
        private DurationBasis durationBasis;
        private int index;
        private int durationType;
        private int duration;

        private String highKey;
        private String lowKey;
        private boolean highLowKeyAvailable;

        public DurationData(DurationBasis durationBasis, int index) {
            this.durationBasis = durationBasis;
            this.index = index;
            highLowKeyAvailable = false;
        }

        public DurationData(DurationBasis durationBasis, int durationType, int duration) {
            this.durationBasis = durationBasis;
            this.durationType = durationType;
            this.duration = duration;
            highLowKeyAvailable = false;
        }

        public DurationData(DurationBasis durationBasis, int durationType, int duration, String highKey, String lowKey) {
            this.durationBasis = durationBasis;
            this.durationType = durationType;
            this.duration = duration;
            this.highKey = highKey;
            this.lowKey = lowKey;
            highLowKeyAvailable = true;
        }

        public DurationBasis getDurationBasis() {
            return durationBasis;
        }

        public int getIndex() {
            return index;
        }

        public int getDurationType() {
            return durationType;
        }

        public int getDuration() {
            return duration;
        }

        public String getHighKey() {
            return highKey;
        }

        public String getLowKey() {
            return lowKey;
        }

        public boolean isHighLowKeyAvailable() {
            return highLowKeyAvailable;
        }
    }
}
