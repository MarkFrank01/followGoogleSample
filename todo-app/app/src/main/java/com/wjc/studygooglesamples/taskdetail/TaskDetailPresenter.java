package com.wjc.studygooglesamples.taskdetail;

/**
 * Created by Administrator on 2017/12/21.
 */

import android.support.annotation.NonNull;

import com.google.common.base.Strings;
import com.wjc.studygooglesamples.data.Task;
import com.wjc.studygooglesamples.data.source.TasksDataSource;
import com.wjc.studygooglesamples.data.source.TasksRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listen to user actions from the UI ({@link TaskDetailFragment}) , retrieves the data and updates
 * the UI as required
 */
public class TaskDetailPresenter implements TaskDetailContract.Presenter {

    private final TasksRepository mTasksRepository;

    private final TaskDetailContract.View mTaskDetailView;

    @NonNull
    private String mTaskId;

    public TaskDetailPresenter(@NonNull TasksRepository mTasksRepository,
                               @NonNull TaskDetailContract.View mTaskDetailView,
                               @NonNull String mTaskId) {
        this.mTasksRepository =checkNotNull( mTasksRepository,"taskRepository cannot be null");
        this.mTaskDetailView = checkNotNull(mTaskDetailView,"taskDetail cannot be null");
        this.mTaskId = mTaskId;

        mTaskDetailView.setPresenter(this);
    }

    @Override
    public void start() {
        openTask();
    }

    private void openTask(){
        if (Strings.isNullOrEmpty(mTaskId)){
            mTaskDetailView.showMissingTask();
            return;
        }

        mTaskDetailView.setLoadingIndicator(true);
        mTasksRepository.getTask(mTaskId, new TasksDataSource.GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                //The view may not be able to handle UI updates anymore
                if (!mTaskDetailView.isActive()){
                    return;
                }
                mTaskDetailView.setLoadingIndicator(false);
                if (null == task){
                    mTaskDetailView.showMissingTask();
                }else {
                    showTask(task);
                }
            }

            @Override
            public void onDataNotAvailable() {
                //The view may not be able to handle UI updates anymore
                if (!mTaskDetailView.isActive()){
                    return;
                }

                mTaskDetailView.showMissingTask();
            }
        });
    }

    @Override
    public void editTask() {
        if (Strings.isNullOrEmpty(mTaskId)){
            mTaskDetailView.showMissingTask();
            return;
        }

        mTaskDetailView.showEditTask(mTaskId);
    }

    @Override
    public void deleteTask() {
        if (Strings.isNullOrEmpty(mTaskId)){
            mTaskDetailView.showMissingTask();
            return;
        }

        mTasksRepository.deleteTask(mTaskId);
        mTaskDetailView.showTaskDeleted();
    }

    @Override
    public void completeTask() {
        if (Strings.isNullOrEmpty(mTaskId)){
            mTaskDetailView.showMissingTask();
            return;
        }

        mTasksRepository.completeTask(mTaskId);
        mTaskDetailView.showTaskMarkedComplete();
    }

    @Override
    public void activateTask() {
        if (Strings.isNullOrEmpty(mTaskId)){
            mTaskDetailView.showMissingTask();
        }

        mTasksRepository.activateTask(mTaskId);
        mTaskDetailView.showTaskMarkedActive();
    }

    private void showTask(@NonNull Task task){
        String title = task.getTitle();
        String description = task.getDescription();

        if (Strings.isNullOrEmpty(title)){
            mTaskDetailView.hideTitle();
        }else {
            mTaskDetailView.showTitle(title);
        }

        if (Strings.isNullOrEmpty(description)){
            mTaskDetailView.hideDescription();
        }else {
            mTaskDetailView.showDescription(description);
        }

        mTaskDetailView.showCompletionStatus(task.isCompleted());
    }
}
