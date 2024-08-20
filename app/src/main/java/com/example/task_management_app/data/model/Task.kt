package com.example.task_management_app.data.model

data class Task(
    val id: String = "",  // Campo para armazenar o ID único
    val name: String,
    var dueDate: Long,
    var completed: Boolean = false,
    val markedOnCalendar: Boolean = false
) {
    // Construtor sem argumentos necessário para o Firebase
    constructor() : this("", "", 0L, false, false)
}
