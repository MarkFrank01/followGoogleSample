package com.wjc.studygooglesamples.statistics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wjc.studygooglesamples.R;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Administrator on 2017/12/22.
 */

/**
 * Main UI for the statistics screen
 */
public class StatisticsFragment extends Fragment implements StatisticsContract.View {

    private TextView mStaticticsTV;

    private StatisticsContract.Presenter mPresenter;

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.statistics_frag, container, false);
        mStaticticsTV = root.findViewById(R.id.statistics);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(StatisticsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void setProgressIndicator(boolean active) {
        if (active) {
            mStaticticsTV.setText(getString(R.string.loading));
        } else {
            mStaticticsTV.setText("");
        }
    }

    @Override
    public void showStatistics(int numberOfIncompleteTasks, int numberOfCompletedTasks) {
        if (numberOfCompletedTasks == 0 && numberOfIncompleteTasks == 0){
            mStaticticsTV.setText(getResources().getString(R.string.statistics_no_tasks));
        }else {
            String displayString = getResources().getString(R.string.statistics_active_tasks) + " "
                    +numberOfIncompleteTasks + "\n"+getResources().getString(R.string.statistics_completed_tasks)+" "+numberOfCompletedTasks;
            mStaticticsTV.setText(displayString);
        }
    }

    @Override
    public void showLoadingStatisticsError() {
        mStaticticsTV.setText(getResources().getString(R.string.statistics_error));
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
