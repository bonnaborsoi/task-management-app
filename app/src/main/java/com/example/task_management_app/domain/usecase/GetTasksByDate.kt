package com.example.task_management_app.domain.usecase

import com.example.task_management_app.data.model.Task
import com.example.task_management_app.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTasksByDate(private val taskRepository: TaskRepository) {
    operator fun invoke(date: Long): Flow<List<Task>> {
        return taskRepository.getTasksByDate(date)
    }
}
