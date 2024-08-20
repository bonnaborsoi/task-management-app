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

        // Encontre o dia 1 do mês atual e a posição dele na semana
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val monthDays = mutableListOf<Day>()

        // Preencher os dias "vazios" antes do dia 1
        for (i in 0 until firstDayOfWeek) {
            monthDays.add(Day(date = 0L, quantity = -1))
        }

        // Preencher os dias do mês
        for (dayOfMonth in 1..daysInMonth) {
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val dayInMillis = calendar.timeInMillis

            val day = highlightedDays.find { it.date == dayInMillis }
                ?: Day(date = dayInMillis, quantity = 0)
            monthDays.add(day)
        }

        // Preencher os dias "vazios" após o último dia do mês
        while (monthDays.size % 7 != 0) {
            monthDays.add(Day(date = 0L, quantity = -1))
        }

        return monthDays
    }
}
