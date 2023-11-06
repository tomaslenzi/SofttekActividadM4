package org.tomas.dao;

import org.tomas.todolist.Task;

import java.util.List;

public interface TaskDao {
    List<Task> getAllTasks();
    void addTask(Task task);
    void updateTask(Task task);
    void deleteTask(int taskId);
}
