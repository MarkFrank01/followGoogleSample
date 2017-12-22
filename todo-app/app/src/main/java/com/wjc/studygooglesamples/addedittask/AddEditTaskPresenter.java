package com.wjc.studygooglesamples.addedittask;

/**
 * Created by Administrator on 2017/12/19.
 */

import android.support.annotation.NonNull;

import com.wjc.studygooglesamples.data.Task;
import com.wjc.studygooglesamples.data.source.TasksDataSource;

/**
 * Listens to user actions from the UI ({@link AddEditTaskFragment}), retrieves the data and updates
 * the UI as required
 */
public class AddEditTaskPresenter implements AddEditTaskContract.Presenter,
        TasksDataSource.GetTaskCallback {

    @NonNull
    private final TasksDataSource mTasksRepository;

    @NonNull
    private final AddEditTaskContract.View mAddTaskView;

    @NonNull
    private String mTaskId;

    private boolean mIsDataMissing;

    /**
     * Create a presenter for the edit/add view
     *
     * @param mTasksRepository       a repository of data for tasks
     * @param mAddTaskView           the edit/add view
     * @param mTaskId                ID of the task to edit or null for a new  task
     * @param shouldLoadDataFromRepo whether data needs to be loaded or not (for config change)
     */
    public AddEditTaskPresenter(@NonNull TasksDataSource mTasksRepository, @NonNull AddEditTaskContract.View mAddTaskView,
                                @NonNull String mTaskId, boolean shouldLoadDataFromRepo) {
        this.mTasksRepository = mTasksRepository;
        this.mAddTaskView = mAddTaskView;
        this.mTaskId = mTaskId;
        mIsDataMissing = shouldLoadDataFromRepo;

        mAddTaskView.setPresenter(this);
    }

    @Override
    public void start() {
        if (!isNewTask() && isDataMissing()) {
            populateTask();
        }
    }

    @Override
    public void saveTask(String title, String description) {
        if (isNewTask()) {
            createTask(title, description);
        } else {
            updateTask(title, description);
        }
    }

    @Override
    public void populateTask() {
        if (isNewTask()) {
            throw new RuntimeException("populateTask() was called but task is new");
        }
        mTasksRepository.getTask(mTaskId, this);
    }

    @Override
    public boolean isDataMissing() {
        return mIsDataMissing;
    }

    @Override
    public void onTaskLoaded(Task task) {
        //The view may not be able to handle UI updates anymore
        if (mAddTaskView.isActive()) {
            mAddTaskView.setTitle(task.getTitle());
            mAddTaskView.setDescription(task.getDescription());
        }
        mIsDataMissing = false;
    }

    @Override
    public void onDataNotAvailable() {
        //The view may not be able to handle UI updates anymore
        if (mAddTaskView.isActive()) {
            mAddTaskView.showEmptyTaskError();
        }
    }

    private boolean isNewTask() {
        return mTaskId == null;
    }

    private void createTask(String title, String description) {
        Task newTask = new Task(title, description);
        if (newTask.isEmpty()) {
            mAddTaskView.showEmptyTaskError();
        } else {
            mTasksRepository.saveTask(newTask);
            mAddTaskView.showTasksList();
        }
    }

    private void updateTask(String title, String description) {
        if (isNewTask()) {
            throw new RuntimeException("updateTask was called but task is nww ");
        }
        mTasksRepository.saveTask(new Task(title, description, mTaskId));
        mAddTaskView.showTasksList(); // After an edit  , back to the list;
    }
}
