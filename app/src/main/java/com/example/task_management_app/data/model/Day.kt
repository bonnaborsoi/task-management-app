package com.example.task_management_app.data.model

data class Day(
    val date: Long,
    var quantity: Int
){
    // Construtor sem argumentos para o Firebase
    constructor() : this(0L, 0)
}