package com.example.task_management_app.domain.usecase

import com.example.task_management_app.data.model.Task
import com.example.task_management_app.data.repository.TaskRepository

class CreateTask(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(task: Task): String {
        // Adiciona lógica de negócios específica aqui, se necessário
        return taskRepository.addTask(task)
    }
}
