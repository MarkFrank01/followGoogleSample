package com.wjc.studygooglesamples.tasks;

import android.support.annotation.NonNull;

import com.wjc.studygooglesamples.data.Task;
import com.wjc.studygooglesamples.data.source.TasksDataSource;
import com.wjc.studygooglesamples.data.source.TasksRepository;
import com.wjc.studygooglesamples.util.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Administrator on 2017/12/2.
 */

public class TasksPresenter implements TasksContract.Presenter {

    private final TasksRepository mTasksRepository;

    private final TasksContract.View mTasksView;

    private TasksFilterType mCurrentFiltering = TasksFilterType.ALL_TASKS;

    private boolean mFirstLoad = true;

    public TasksPresenter(@NonNull TasksRepository mTasksRepository, @NonNull TasksContract.View mTasksView) {
        this.mTasksRepository = checkNotNull(mTasksRepository, "tasksRepository cannot be null");
        this.mTasksView = checkNotNull(mTasksView, "tasksView cannot be null!");

        mTasksView.setPresenter(this);
    }

    @Override
    public void start() {
        loadTasks(false);
    }

    @Override
    public void result(int request, int resultCode) {
        //如果成功添加了任务，则显示snackbar

    }

    @Override
    public void loadTasks(boolean forceUpdate) {
        //简化示例:在第一次加载时将强制重新加载网络
        loadTasks(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link TasksDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadTasks(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mTasksView.setLoadingIndicator(true);
        }
        if (forceUpdate) {
            mTasksRepository.refreshTasks();
        }

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        mTasksRepository.getTasks(new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                List<Task> tasksToShow = new ArrayList<Task>();

                // This callback may be called twice, once for the cache and once for loading
                // the data from the server API, so we check before decrementing, otherwise
                // it throws "Counter has been corrupted!" exception.
                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement(); // Set app as idle.
                }

                // We filter the tasks based on the requestType
                for (Task task : tasks) {
                    switch (mCurrentFiltering) {
                        case ALL_TASKS:
                            tasksToShow.add(task);
                            break;
                        case ACTIVE_TASKS:
                            if (task.isActive()) {
                                tasksToShow.add(task);
                            }
                            break;
                        case COMPLETED_TASKS:
                            if (task.isCompleted()) {
                                tasksToShow.add(task);
                            }
                            break;
                        default:
                            tasksToShow.add(task);
                            break;
                    }
                }
                // The view may not be able to handle UI updates anymore
                if (!mTasksView.isActive()) {
                    return;
                }
                if (showLoadingUI) {
                    mTasksView.setLoadingIndicator(false);
                }

                processTasks(tasksToShow);
            }

            private void processTasks(List<Task> tasks) {
                if (tasks.isEmpty()) {
                    // Show a message indicating there are no tasks for that filter type.
                    processEmptyTasks();
                } else {
                    // Show the list of tasks
                    mTasksView.showTasks(tasks);
                    // Set the filter label's text.
                    showFilterLabel();
                }
            }

            private void showFilterLabel() {
                switch (mCurrentFiltering) {
                    case ACTIVE_TASKS:
                        mTasksView.showActiveFilterLabel();
                        break;
                    case COMPLETED_TASKS:
                        mTasksView.showCompletedFilterLabel();
                        break;
                    default:
                        mTasksView.showAllFilterLabel();
                        break;
                }
            }

            private void processEmptyTasks() {
                switch (mCurrentFiltering) {
                    case ACTIVE_TASKS:
                        mTasksView.showNoActiveTasks();
                        break;
                    case COMPLETED_TASKS:
                        mTasksView.showNoCompletedTasks();
                        break;
                    default:
                        mTasksView.showNoTasks();
                        break;
                }
            }

            @Override
            public void onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!mTasksView.isActive()) {
                    return;
                }
                mTasksView.showLoadingTasksError();
            }
        });
    }


    @Override
    public void addNewTask() {
        mTasksView.showAddTask();
    }

    @Override
    public void openTaskDetails(@NonNull Task requestedTask) {
        checkNotNull(requestedTask, "requestedTask cannot be null!");
        mTasksView.showTaskDetailsUi(requestedTask.getId());
    }

    @Override
    public void completeTask(@NonNull Task completedTask) {
        checkNotNull(completedTask,"completedTask cannot be null!");
        mTasksRepository.completeTask(completedTask);
        mTasksView.showTaskMarkedComplete();
        loadTasks(false,false);
    }

    @Override
    public void activateTask(@NonNull Task activeTask) {
        checkNotNull(activeTask,"activeTask can't be null");
        mTasksRepository.activateTask(activeTask);
        mTasksView.showTaskMarkedComplete();
        loadTasks(false,false);
    }

    @Override
    public void clearCompletedTasks() {
        mTasksRepository.clearCompletedTasks();
        mTasksView.showCompletedFilterLabel();
        loadTasks(false,false);
    }

    /**
     * 设置当前任务过滤类型。
     * @param requestType Can be {@link TasksFilterType#ALL_TASKS}.
     *                    {@link TasksFilterType#COMPLETED_TASKS}.or
     *                    {@link TasksFilterType#ACTIVE_TASKS}
     */

    @Override
    public void setFiltering(TasksFilterType requestType) {
        mCurrentFiltering = requestType;
    }

    @Override
    public TasksFilterType getFiltering() {
        return mCurrentFiltering;
    }
}
