package com.wjc.studygooglesamples.addedittask;

/**
 * Created by Administrator on 2017/12/19.
 */

import com.wjc.studygooglesamples.BasePresenter;
import com.wjc.studygooglesamples.BaseView;

/**
 * this specifies the contract between view and presenter
 */
public interface AddEditTaskContract {

    interface View extends BaseView<Presenter> {

        void showEmptyTaskError();

        void showTasksList();

        void setTitle(String title);

        void setDescription(String description);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void saveTask(String title, String description);

        void populateTask();

        boolean isDataMissing();
    }
}
