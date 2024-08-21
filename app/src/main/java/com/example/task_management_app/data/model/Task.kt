package com.example.task_management_app.data.model

data class Task(
    val id: String = "",
    val name: String,
    var dueDate: Long,
    var completed: Boolean = false,
    val markedOnCalendar: Boolean = false,
    var location: String = "None",
    val latLng: CustomLatLng = CustomLatLng()
) {
    constructor() : this("", "", 0L, false, false, "None", CustomLatLng())
}
