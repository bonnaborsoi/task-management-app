package com.example.task_management_app.ui.tasklist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_management_app.domain.usecase.GetAllTasks
import com.example.task_management_app.data.model.Task
import kotlinx.coroutines.flow.*
import java.util.Calendar

class TaskListViewModel(
    private val getAllTasks: GetAllTasks
) : ViewModel() {

    private val _filterDate = MutableStateFlow<Long?>(null)
    val filterDate: StateFlow<Long?> get() = _filterDate
    val tasks: StateFlow<List<Task>> = _filterDate
        .flatMapLatest { date ->
            if (date != null) {
                Log.d("TaskListViewModel", "Filtering tasks for date: $date")
                getAllTasks().map { tasks ->
                    tasks.filter { task ->
                        // Ajuste a comparação para ignorar horas
                        val taskDate = Calendar.getInstance().apply { timeInMillis = task.dueDate }
                        val filterDate = Calendar.getInstance().apply { timeInMillis = date }

                        taskDate.get(Calendar.YEAR) == filterDate.get(Calendar.YEAR) &&
                                taskDate.get(Calendar.MONTH) == filterDate.get(Calendar.MONTH) &&
                                taskDate.get(Calendar.DAY_OF_MONTH) == filterDate.get(Calendar.DAY_OF_MONTH)
                    }
                }
            } else {
                Log.d("TaskListViewModel", "No filter date set.")
                getAllTasks()
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun filterTasksByDate(date: Long) {
        Log.d("TaskListViewModel", "Set filter date to: $date")
        _filterDate.value = date
    }

    fun clearFilter() {
        Log.d("TaskListViewModel", "Clearing filter.")
        _filterDate.value = null
    }
}