package com.wjc.studygooglesamples.statistics;

/**
 * Created by Administrator on 2017/12/22.
 */

import com.wjc.studygooglesamples.BasePresenter;
import com.wjc.studygooglesamples.BaseView;

/**
 * This specifies the contract between the view and the presenter
 */
public interface StatisticsContract {

    interface View extends BaseView<Presenter>{

        void setProgressIndicator(boolean active);

        void showStatistics(int numberOfIncompleteTasks, int numberOfCompletedTasks);

        void showLoadingStatisticsError();

        boolean isActive();
    }

    interface Presenter extends BasePresenter{

    }
}
