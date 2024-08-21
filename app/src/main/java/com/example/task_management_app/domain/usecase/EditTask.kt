package com.example.task_management_app.domain.usecase

import com.example.task_management_app.data.model.Task
import com.example.task_management_app.data.repository.TaskRepository

class EditTask(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(task: Task): Boolean {
        return taskRepository.editTask(task)
    }
}
