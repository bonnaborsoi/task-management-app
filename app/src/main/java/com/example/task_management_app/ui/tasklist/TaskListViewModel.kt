package com.example.task_management_app.ui.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_management_app.domain.usecase.GetAllTasks
import com.example.task_management_app.data.model.Task
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class TaskListViewModel(
    private val getAllTasks: GetAllTasks
) : ViewModel() {

    val tasks: StateFlow<List<Task>> = getAllTasks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}