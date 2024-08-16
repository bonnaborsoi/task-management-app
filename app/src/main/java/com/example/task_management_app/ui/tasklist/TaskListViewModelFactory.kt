package com.example.task_management_app.ui.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.task_management_app.domain.usecase.GetAllTasks

class TaskListViewModelFactory(
    private val getAllTasks: GetAllTasks
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskListViewModel(getAllTasks) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
