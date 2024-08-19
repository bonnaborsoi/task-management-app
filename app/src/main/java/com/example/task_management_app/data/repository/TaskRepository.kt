package com.example.task_management_app.data.repository

import com.example.task_management_app.data.model.Task

import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun addTask(task: Task): String
    suspend fun editTask(task: Task): Boolean
    suspend fun deleteTask(task: Task): Boolean
    fun getTasksByDate(date: Long): Flow<List<Task>>
    fun getAllTasks(): Flow<List<Task>>
}
