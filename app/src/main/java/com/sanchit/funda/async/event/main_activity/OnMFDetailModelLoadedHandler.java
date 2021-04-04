package com.sanchit.funda.async.event.main_activity;

import android.view.View;

import com.sanchit.funda.MainActivity;
import com.sanchit.funda.adapter.HomeSummaryTopFundsAdapter;
import com.sanchit.funda.model.MFDetailModel;
import com.sanchit.funda.model.factory.DataFactory;
import com.sanchit.funda.model.factory.MFDetailModelFactory;

import java.util.Collections;
import java.util.List;

public class OnMFDetailModelLoadedHandler extends AbstractMainActivityEventHandler  implements MFDetailModelFactory.OnCompletionHandler {

    public OnMFDetailModelLoadedHandler(MainActivity activity) {
        super(activity);
    }

    @Override
    public void onMFDetailDatasetLoaded(List<MFDetailModel> mfDetailModelList) {
        activity.mfDetailModel.clear();
        activity.mfDetailModel.addAll(mfDetailModelList);
        Collections.sort(activity.mfDetailModel, (o1, o2) -> o1.getPriceModel().get1YearReturnComparable().compareTo(o2.getPriceModel().get1YearReturnComparable()) * -1);

        if (activity.topFundsModel == null) {
            activity.topFundsModel = DataFactory.generateHomeSummaryTopFundsModel(activity.mfDetailModel);
            activity.homeSummaryTopFundsAdapter = new HomeSummaryTopFundsAdapter(activity, activity.topFundsModel);
            activity.recylerViewTopFunds.setAdapter(activity.homeSummaryTopFundsAdapter);
        } else {
            activity.topFundsModel.clear();
            activity.topFundsModel.addAll(DataFactory.generateHomeSummaryTopFundsModel(activity.mfDetailModel));
            activity.homeSummaryTopFundsAdapter.notifyDataSetChanged();
        }

        activity.progressBarTopFunds.setVisibility(View.INVISIBLE);
    }
}