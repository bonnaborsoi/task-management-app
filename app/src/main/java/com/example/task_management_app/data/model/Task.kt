package com.example.task_management_app.data.model

data class Task(
    val id: String? = "",
    val name: String,
    val dueDate: Long,
    val completed: Boolean = false,
    val markedOnCalendar: Boolean = false
){
    // Construtor sem argumentos necessário para o Firebase
    constructor() : this("", "", 0L, false, false)
}
