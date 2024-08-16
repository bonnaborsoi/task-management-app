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
        val newTaskRef = databaseReference.push() // Cria uma nova referência com um ID único
        firebaseService.setValue(newTaskRef, task) // Utiliza o FirebaseService para salvar a tarefa

        // Se a task estiver marcada no calendário, incrementa o dia correspondente
        if (task.markedOnCalendar) {
            calendarDayRepository.incrementDay(task.dueDate)
        }

        return newTaskRef.key ?: throw IllegalStateException("Task ID not generated")
    }

    override suspend fun editTask(task: Task): Boolean {
        return try {
            task.id?.let {
                val taskRef = databaseReference.child(it)

                // Verificando se o campo isMarkedOnCalendar mudou
                val previousTaskSnapshot = taskRef.get().await()
                val previousTask = previousTaskSnapshot.getValue(Task::class.java)

                // Se o task foi marcada no calendário após a edição, incrementa o dia
                if (previousTask?.markedOnCalendar == false && task.markedOnCalendar) {
                    calendarDayRepository.incrementDay(task.dueDate)
                }

                // Se o task foi desmarcada no calendário após a edição, decrementa o dia
                if (previousTask?.markedOnCalendar == true && !task.markedOnCalendar) {
                    calendarDayRepository.decrementDay(task.dueDate)
                }

                // Atualiza a tarefa
                firebaseService.setValue(taskRef, task)
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteTask(taskId: String): Boolean {
        return try {
            val taskRef = databaseReference.child(taskId)
            val taskSnapshot = taskRef.get().await()
            val task = taskSnapshot.getValue(Task::class.java)
            Log.d("TaskRepositoryImpl", "Dados brutos do Firebase: ${taskSnapshot.value}")
            val isMarkedOnCalendarDirect = taskSnapshot.child("MarkedOnCalendar").getValue(Boolean::class.java)
            Log.d("TaskRepositoryImpl", "Valor direto de MarkedOnCalendar: $isMarkedOnCalendarDirect")

            // Adicionando log para verificar o valor de task?.isMarkedOnCalendar
            Log.d("TaskRepositoryImpl", "Valor de task?.isMarkedOnCalendar: ${task?.markedOnCalendar}")
            if (task?.markedOnCalendar == true) {
                Log.d("TaskRepositoryImpl", "é True") // Log adicional se for verdadeiro
            }
            // Se a task estava marcada no calendário, decrementa o dia correspondente
            if (task?.markedOnCalendar == true) {
                calendarDayRepository.decrementDay(task.dueDate)
            }

            // Deleta a tarefa
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