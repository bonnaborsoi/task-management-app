package com.example.task_management_app.ui.calendar


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.task_management_app.domain.usecase.getAllDays

class CalendarViewModelFactory(
    private val getAllDays: getAllDays
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            return CalendarViewModel(getAllDays) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}