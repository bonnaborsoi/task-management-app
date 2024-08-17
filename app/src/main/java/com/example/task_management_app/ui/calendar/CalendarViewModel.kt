package com.example.task_management_app.ui.calendar

import android.util.Log
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

    val days: StateFlow<List<Day>> = currentDate.flatMapLatest { date ->
        getAllDays().map { highlightedDays ->
            Log.d("CalendarViewModel", "Highlighted Days: $highlightedDays")
            generateMonthDays(date, highlightedDays)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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

    private fun generateMonthDays(currentDate: Long, highlightedDays: List<Day>): List<Day> {
        val calendar = Calendar.getInstance().apply { timeInMillis = currentDate }
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val totalDays = 42 // 6 semanas de exibição (7 dias por semana)

        val monthDays = mutableListOf<Day>()
        for (i in 0 until totalDays) {
            if (i >= firstDayOfWeek && i < firstDayOfWeek + daysInMonth) {
                val dayOfMonth = i - firstDayOfWeek + 1
                val dayCalendar = Calendar.getInstance().apply {
                    timeInMillis = currentDate
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                val dayInMillis = dayCalendar.timeInMillis

                val day = highlightedDays.find { it.date == dayInMillis }
                    ?: Day(date = dayInMillis, quantity = 0)
                monthDays.add(day)
            } else {
                monthDays.add(Day(date = 0L, quantity = 0)) // Células vazias
            }
        }
        return monthDays
    }
}