package com.example.task_management_app.domain.usecase

import com.example.task_management_app.data.model.Task
import com.example.task_management_app.data.model.Day
import com.example.task_management_app.data.repository.CalendarDayRepository

class decrementDay(private val calendardayRepository: CalendarDayRepository) {
    suspend operator fun invoke(date: Long): Boolean {
        return calendardayRepository.decrementDay(date)
    }
}