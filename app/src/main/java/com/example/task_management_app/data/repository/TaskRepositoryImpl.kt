package com.example.task_management_app.data.repository

import android.util.Log
import com.example.task_management_app.data.model.Task
import com.example.task_management_app.data.firebase.FirebaseService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskRepositoryImpl(
    private val firebaseService: FirebaseService,
    private val calendarDayRepository: CalendarDayRepository // Injetando o repositório de dias
) : TaskRepository {

    private val databaseReference = firebaseService.getDatabaseReference("tasks")

    override suspend fun addTask(task: Task): String {
        val newTaskRef = databaseReference.push()
        val taskWithId = task.copy(id = newTaskRef.key ?: "")
        firebaseService.setValue(newTaskRef, taskWithId)
        if (taskWithId.markedOnCalendar) {
            calendarDayRepository.incrementDay(taskWithId.dueDate)
        }

        return newTaskRef.key ?: throw IllegalStateException("Task ID not generated")
    }

    override suspend fun editTask(task: Task): Boolean {
        return try {
            val taskRef = databaseReference.child(task.id)

            val previousTaskSnapshot = taskRef.get().await()
            val previousTask = previousTaskSnapshot.getValue(Task::class.java)

            if (previousTask?.markedOnCalendar == false && task.markedOnCalendar) {  // Se o task foi marcada no calendário após a edição, incrementa o dia
                calendarDayRepository.incrementDay(task.dueDate)
            } else if (previousTask?.markedOnCalendar == true && !task.markedOnCalendar) { // Se o task foi desmarcada no calendário após a edição, decrementa o dia
                calendarDayRepository.decrementDay(previousTask.dueDate)
            } else if (previousTask?.markedOnCalendar == true && task.markedOnCalendar){
                if (previousTask?.dueDate != task.dueDate){
                    if (previousTask != null) {
                        calendarDayRepository.decrementDay(previousTask.dueDate)
                    }
                    if (task.markedOnCalendar){
                        calendarDayRepository.incrementDay(task.dueDate)
                    }
                }
            }

            firebaseService.setValue(taskRef, task)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteTask(task: Task): Boolean {
        return try {
            val taskRef = databaseReference.child(task.id)

            val taskSnapshot = taskRef.get().await()
            val task = taskSnapshot.getValue(Task::class.java)

            if (task?.markedOnCalendar == true) {
                Log.d("PRINT","EXCLUI TAREFA")
                calendarDayRepository.decrementDay(task.dueDate)
            }

            firebaseService.deleteValue(taskRef)
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