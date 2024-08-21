package com.example.task_management_app.data.model

data class Task(
    val id: String = "",
    val name: String,
    var dueDate: Long,
    var completed: Boolean = false,
    val markedOnCalendar: Boolean = false,
    var location: String = "None",
    var latitude: Double? = null,
    var longitude: Double? = null
) {
    // Construtor sem argumentos necessário para o Firebase
    constructor() : this("", "", 0L, false, false, "None",null,null)
}
