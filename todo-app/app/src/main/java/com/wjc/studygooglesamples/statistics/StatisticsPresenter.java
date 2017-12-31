package com.wjc.studygooglesamples.statistics;

/**
 * Created by Administrator on 2017/12/22.
 */

import com.wjc.studygooglesamples.data.Task;
import com.wjc.studygooglesamples.data.source.TasksDataSource;
import com.wjc.studygooglesamples.data.source.TasksRepository;
import com.wjc.studygooglesamples.util.EspressoIdlingResource;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user action from the UI ({@link StatisticsFragment}), retrieves the data and updates
 * the UI as required
 */
public class StatisticsPresenter implements StatisticsContract.Presenter {

    private final TasksRepository mTaskRepository;

    private final StatisticsContract.View mStatisticsView;

    public StatisticsPresenter(TasksRepository mTaskRepository, StatisticsContract.View mStatisticsView) {
        this.mTaskRepository = checkNotNull(mTaskRepository, "taskRepository cannot be null");
        this.mStatisticsView = checkNotNull(mStatisticsView, "Statistics cannot be null");

        mStatisticsView.setPresenter(this);
    }

    @Override
    public void start() {
        loadStatistics();
    }

    private void loadStatistics() {
        mStatisticsView.setProgressIndicator(true);

        EspressoIdlingResource.increment();

        mTaskRepository.getTasks(new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                int activeTasks = 0;
                int completeTasks = 0;

                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement();
                }

                for (Task task : tasks) {
                    if (task.isCompleted()) {
                        completeTasks += 1;
                    } else {
                        activeTasks += 1;
                    }
                }

                if (!mStatisticsView.isActive()) {
                    return;
                }

                mStatisticsView.setProgressIndicator(false);

                mStatisticsView.showStatistics(activeTasks, completeTasks);
            }

            @Override
            public void onDataNotAvailable() {
                if (!mStatisticsView.isActive()) {
                    return;
                }
                mStatisticsView.showLoadingStatisticsError();
            }
        });
    }
}
