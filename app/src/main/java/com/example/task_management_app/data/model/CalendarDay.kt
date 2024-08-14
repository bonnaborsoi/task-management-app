package com.example.task_management_app.data.model

data class CalendarDay(
    val date: Long,                  // Data específica representada como timestamp Unix
    val isHighlighted: Boolean = false // Se o dia deve ser destacado (ou seja, tem tarefas marcadas)
)
