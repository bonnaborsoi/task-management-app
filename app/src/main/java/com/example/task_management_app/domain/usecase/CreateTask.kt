package com.example.task_management_app.domain.usecase

import com.example.task_management_app.data.model.Task
import com.example.task_management_app.data.repository.TaskRepository

class CreateTask(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) {
        repository.addTask(task)
    }
}
