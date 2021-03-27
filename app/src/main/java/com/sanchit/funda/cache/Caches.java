package com.sanchit.funda.cache;

public interface Caches {

    String FUNDS = "com.sanchit.funda.FUNDS";
    String FUNDS_BY_NAME = "com.sanchit.funda.FUNDS_BY_NAME";
    String FUNDS_BY_AMFI_ID = "com.sanchit.funda.FUNDS_BY_AMFI_ID";

    String PRICES_BY_AMFI_ID = "com.sanchit.funda.PRICES_BY_AMFI_ID";

    String POSITIONS = "com.sanchit.funda.POSITIONS";

    String TRADES = "com.sanchit.funda.TRADES";
    String TRADES_BY_AMFI_ID = "com.sanchit.funda.TRADES_BY_AMFI_ID";

    String VIEW_DATA = "com.sanchit.funda.VIEWS.data";

    String CASHFLOW_POSITION_RAW = "com.sanchit.funda.CASHFLOW_POSITION_RAW";
    String CASHFLOW_POSITION_BY_AMFI_ID = "com.sanchit.funda.CASHFLOW_POSITION_BY_AMFI_ID";
}
