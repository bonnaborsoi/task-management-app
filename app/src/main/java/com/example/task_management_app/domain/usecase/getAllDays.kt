package com.example.task_management_app.domain.usecase

import com.example.task_management_app.data.model.Day
import kotlinx.coroutines.flow.Flow


import com.example.task_management_app.data.repository.CalendarDayRepository

class getAllDays(private val calendardayRepository: CalendarDayRepository) {
    operator fun invoke(): Flow<List<Day>> {
        return calendardayRepository.getAllDays()
    }
}
