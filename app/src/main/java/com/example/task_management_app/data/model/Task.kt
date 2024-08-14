package com.example.task_management_app.data.model

data class Task(
    val id: String? = "",
    val name: String,
    val dueDate: Long,
    val isCompleted: Boolean = false,
    val isMarkedOnCalendar: Boolean = false
)
