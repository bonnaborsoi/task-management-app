package com.example.task_management_app.domain.usecase

import com.example.task_management_app.data.repository.CalendarDayRepository

class incrementDay(private val calendardayRepository: CalendarDayRepository) {
    suspend operator fun invoke(date: Long): Boolean {
        return calendardayRepository.incrementDay(date)
    }
}
