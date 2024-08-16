package com.example.task_management_app.data.repository

import com.example.task_management_app.data.model.Day

import kotlinx.coroutines.flow.Flow

interface CalendarDayRepository {
    suspend fun incrementDay(date: Long): Boolean
    suspend fun decrementDay(date: Long): Boolean
    fun getAllDays(): Flow<List<Day>>
}