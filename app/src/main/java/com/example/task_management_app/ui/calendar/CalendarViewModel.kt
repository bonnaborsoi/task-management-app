package com.example.task_management_app.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_management_app.data.model.Day
import com.example.task_management_app.domain.usecase.getAllDays
import kotlinx.coroutines.flow.*
import java.util.*

class CalendarViewModel(
    private val getAllDays: getAllDays
) : ViewModel() {

    private val _currentDate = MutableStateFlow(Calendar.getInstance().timeInMillis)
    val currentDate: StateFlow<Long> = _currentDate.asStateFlow()

    val days: StateFlow<List<Day>> = getAllDays()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun goToNextMonth() {
        _currentDate.value = Calendar.getInstance().apply {
            timeInMillis = currentDate.value
            add(Calendar.MONTH, 1)
        }.timeInMillis
    }

    fun goToPreviousMonth() {
        _currentDate.value = Calendar.getInstance().apply {
            timeInMillis = currentDate.value
            add(Calendar.MONTH, -1)
        }.timeInMillis
    }
}