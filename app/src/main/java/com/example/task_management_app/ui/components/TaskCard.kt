package com.example.task_management_app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.task_management_app.data.model.Task

@Composable
fun TaskCard(task: Task, modifier: Modifier = Modifier) {
    Card(modifier = modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task.name)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Data de Expiração: ${task.dueDate}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = if (task.completed) "Concluída" else "Pendente")
        }
    }
}

@Preview
@Composable
fun TaskCardPreview() {
    TaskCard(task = Task("1", "Tarefa Teste", System.currentTimeMillis(), false, true))
}
