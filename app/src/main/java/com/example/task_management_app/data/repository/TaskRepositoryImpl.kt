package com.example.task_management_app.data.repository

import com.example.task_management_app.data.model.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import com.example.task_management_app.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.task_management_app.data.firebase.FirebaseService

class TaskRepositoryImpl(
    private val firebaseService: FirebaseService
) : TaskRepository {

    private val databaseReference = firebaseService.getDatabaseReference("tasks")

    override suspend fun addTask(task: Task): String {
        val newTaskRef = databaseReference.push() // Cria uma nova referência com um ID único
        firebaseService.setValue(newTaskRef, task) // Utiliza o FirebaseService para salvar a tarefa
        return newTaskRef.key ?: throw IllegalStateException("Task ID not generated")
    }

    override suspend fun editTask(task: Task): Boolean {
        return try {
            task.id?.let {
                val taskRef = databaseReference.child(it)
                firebaseService.setValue(taskRef, task) // Utiliza o FirebaseService para atualizar a tarefa
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteTask(taskId: String): Boolean {
        return try {
            val taskRef = databaseReference.child(taskId)
            firebaseService.deleteValue(taskRef) // Utiliza o FirebaseService para deletar a tarefa
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getTasksByDate(date: Long): Flow<List<Task>> = flow {
        val dateString = convertDateLongToString(date)
        val snapshot = databaseReference.orderByChild("dueDate").equalTo(dateString).get().await()
        val tasks = snapshot.children.mapNotNull { it.getValue(Task::class.java) }
        emit(tasks)
    }

    override fun getAllTasks(): Flow<List<Task>> = flow {
        val snapshot = databaseReference.get().await()
        val tasks = snapshot.children.mapNotNull { it.getValue(Task::class.java) }
        emit(tasks)
    }

    private fun convertDateLongToString(date: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(date))
    }
}
