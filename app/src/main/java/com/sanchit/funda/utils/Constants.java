package com.sanchit.funda.utils;

import com.sanchit.funda.R;
import com.sanchit.funda.model.PositionViewModel;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static final BigDecimal EMPTY_PRICE = new BigDecimal(-1);

    public static final Map<String, Integer> PRICE_MAP = new HashMap<>();

    private static final Map<Integer, Map<String, Comparator<PositionViewModel>>> POSITIONS_VIEW_COMPARATOR_MAP = new HashMap<>();
    private static final Map<Integer, Integer> POSITIONS_VIEW_SORT_ICON_MAP = new HashMap<>();

    static {
        PRICE_MAP.put(Constants.Duration.T, 1);
        PRICE_MAP.put(Constants.Duration.T_1, 2);
        PRICE_MAP.put(Constants.Duration.T_10, 10);
        PRICE_MAP.put(Constants.Duration.T_30, 30);
        PRICE_MAP.put(Constants.Duration.T_90, 90);
        PRICE_MAP.put(Constants.Duration.T_180, 180);
        PRICE_MAP.put(Constants.Duration.T_365, 365);

        POSITIONS_VIEW_COMPARATOR_MAP.put(R.id.positions_view_header_fund, newMap((o1, o2) -> o1.getHead().compareTo(o2.getHead()), (o1, o2) -> o2.getHead().compareTo(o1.getHead())));
        POSITIONS_VIEW_COMPARATOR_MAP.put(R.id.positions_view_header_cost, newMap((o1, o2) -> o1.getInvestment().compareTo(o2.getInvestment()), (o1, o2) -> o2.getInvestment().compareTo(o1.getInvestment())));
        POSITIONS_VIEW_COMPARATOR_MAP.put(R.id.positions_view_header_valuation, newMap((o1, o2) -> o1.getValuation().compareTo(o2.getValuation()), (o1, o2) -> o2.getValuation().compareTo(o1.getValuation())));
        POSITIONS_VIEW_COMPARATOR_MAP.put(R.id.positions_view_header_daypnl, newMap((o1, o2) -> o1.getPnlDay().compareTo(o2.getPnlDay()), (o1, o2) -> o2.getPnlDay().compareTo(o1.getPnlDay())));
        POSITIONS_VIEW_COMPARATOR_MAP.put(R.id.positions_view_header_overallpnl, newMap((o1, o2) -> o1.getPnlOverall().compareTo(o2.getPnlOverall()), (o1, o2) -> o2.getPnlOverall().compareTo(o1.getPnlOverall())));

        POSITIONS_VIEW_SORT_ICON_MAP.put(R.id.positions_view_header_fund, R.id.positions_view_header_fund_sort_icon);
        POSITIONS_VIEW_SORT_ICON_MAP.put(R.id.positions_view_header_valuation, R.id.positions_view_header_valuation_sort_icon);
        POSITIONS_VIEW_SORT_ICON_MAP.put(R.id.positions_view_header_cost, R.id.positions_view_header_cost_sort_icon);
        POSITIONS_VIEW_SORT_ICON_MAP.put(R.id.positions_view_header_daypnl, R.id.positions_view_header_daypnl_sort_icon);
        POSITIONS_VIEW_SORT_ICON_MAP.put(R.id.positions_view_header_overallpnl, R.id.positions_view_header_overallpnl_sort_icon);
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

    public static final Integer getSortImage(Integer viewId) {
        return POSITIONS_VIEW_SORT_ICON_MAP.get(viewId);
    }

    public static final Collection<Integer> getAllSortImages() {
        return POSITIONS_VIEW_SORT_ICON_MAP.values();
    }

    public static interface SortType {
        String Ascending = "A";
        String Descending = "D";
    }

    public static final class Duration {
        public static final String T = "T";
        public static final String T_1 = "T Minus 1";
        public static final String T_10 = "T Minus 10";
        public static final String T_30 = "T Minus 30";
        public static final String T_90 = "T Minus 90";
        public static final String T_180 = "T Minus 180";
        public static final String T_365 = "T Minus 365";
    }
}
