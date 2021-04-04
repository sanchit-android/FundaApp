package com.sanchit.funda.async.event.main_activity;

import android.net.Uri;

import com.sanchit.funda.MainActivity;
import com.sanchit.funda.async.FundsRawDataAsyncLoader;
import com.sanchit.funda.async.GrowwStatementAsyncLoader;
import com.sanchit.funda.async.NSDL_CASAsyncLoader;
import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.dao.MutualFundDao;
import com.sanchit.funda.dao.entity.MutualFundModel;
import com.sanchit.funda.dao.task.SelectActionTask;
import com.sanchit.funda.database.AppDatabase;
import com.sanchit.funda.database.DatabaseHelper;
import com.sanchit.funda.log.LogManager;
import com.sanchit.funda.model.MutualFund;
import com.sanchit.funda.model.factory.MFDetailModelFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FundsRawDataLoadedHandler extends AbstractMainActivityEventHandler implements OnEnrichmentCompleted<List<MutualFund>>, SelectActionTask.SelectActionCallback<MutualFundModel> {

    public FundsRawDataLoadedHandler(MainActivity activity) {
        super(activity);
    }

    @Override
    // Fetched from File handler
    public void updateView(List<MutualFund> data) {
        LogManager.log("Loaded " + data.size() + " funds from raw file");

        MutualFundDao dao = AppDatabase.getInstance(activity).mutualFundDao();
        MutualFundModel[] funds = new MutualFundModel[data.size()];
        int i = 0;
        Set<String> keys = new HashSet<>();
        for (MutualFund item : data) {
            if(item.getAmfiID() == null || "".equals(item.getAmfiID().trim())) {
                LogManager.log("Null Keyed MF to persist - IGNORED [" + item.toString() + "]");
                continue;
            }
            if(keys.contains(item.getAmfiID().trim())) {
                LogManager.log("Duplicate Keyed MF to persist - IGNORED [" + item.toString() + "]");
                continue;
            }
            keys.add(item.getAmfiID());
            funds[i++] = new MutualFundModel(item);
        }
        DatabaseHelper.insert(dao, funds);

        updateAssociatedViews(data);
    }

    @Override
    // Fetched from DB handler
    public void onSelectComplete(List<MutualFundModel> resultSet) {
        if (resultSet == null || resultSet.isEmpty()) {
            new FundsRawDataAsyncLoader(activity, new FundsRawDataLoadedHandler(activity)).execute(Uri.EMPTY);
            return;
        }

        LogManager.log("Loaded " + resultSet.size() + " funds from database");
        MutualFundDao mutualFundDao = AppDatabase.getInstance(activity).mutualFundDao();
        for(MutualFundModel x : resultSet)
            DatabaseHelper.delete(mutualFundDao, x);
        List<MutualFund> funds = new ArrayList<>();
        for (MutualFundModel item : resultSet) {
            funds.add(new MutualFund(item));
        }

        updateAssociatedViews(funds);
    }

    public void updateAssociatedViews(List<MutualFund> data) {
        CacheManager.registerRawData(Caches.FUNDS, data);
        CacheManager.registerCache(Caches.FUNDS_BY_NAME, generateCacheByName(data));
        CacheManager.registerCache(Caches.FUNDS_BY_AMFI_ID, generateCacheByAmfiId(data));

        if ("Groww.in Transaction Statement".equals(activity.fileType)) {
            new GrowwStatementAsyncLoader(activity, new OnGrowwStatementLoadedHandler(activity), activity.PAN).execute(activity.ecasFilePath);
        } else {
            new NSDL_CASAsyncLoader(activity, new OnECASFileLoadedHandler(activity), activity.PAN).execute(activity.ecasFilePath);
        }

        activity.homeSummaryTopFundsAdapter.setGrouping(activity.DEFAULT_CATEGORY);
        new MFDetailModelFactory(activity.DEFAULT_CATEGORY, MFDetailModelFactory.Criteria.FundAppDefCategoryCriteria, new OnMFDetailModelLoadedHandler(activity));
    }
}
