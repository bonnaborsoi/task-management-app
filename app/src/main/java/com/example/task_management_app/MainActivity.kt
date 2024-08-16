package com.example.task_management_app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.task_management_app.data.firebase.FirebaseService
import com.example.task_management_app.data.model.Task
import com.example.task_management_app.data.repository.TaskRepositoryImpl
import com.example.task_management_app.ui.theme.TaskmanagementappTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.task_management_app.domain.usecase.CreateTask
import androidx.fragment.app.commit
import com.example.task_management_app.ui.tasklist.TaskListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var createTask: CreateTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseService = FirebaseService()
        val taskRepository = TaskRepositoryImpl(firebaseService)
        createTask = CreateTask(taskRepository)

        // Cria uma nova task e a adiciona ao Firebase
        val newTask = Task(
            id = null,
            name = "Tigrinho",
            dueDate = System.currentTimeMillis(),
            isCompleted = false,
            isMarkedOnCalendar = true
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val taskId = createTask(newTask) // Usando o caso de uso CreateTask
                Log.d("MainActivity", "Task adicionada com sucesso! ID: $taskId")
            } catch (e: Exception) {
                Log.e("MainActivity", "Erro ao adicionar task", e)
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, TaskListFragment())
            }
        }
    }
}
/*class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            TaskmanagementappTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        // Cria uma instância de FirebaseService e TaskRepositoryImpl
        val firebaseService = FirebaseService()
        val taskRepository = TaskRepositoryImpl(firebaseService)

        // Cria uma nova task e a adiciona ao Firebase
        val newTask = Task(
            id = null, // O ID será gerado pelo Firebase
            name = "Test Task",
            dueDate = System.currentTimeMillis(), // Define a data atual como data de expiração
            isCompleted = false,
            isMarkedOnCalendar = true
        )

        // Utiliza coroutines para fazer operações com o repositório
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val taskId = taskRepository.addTask(newTask) // Adiciona a task ao Firebase
                Log.d("MainActivity", "Task adicionada com sucesso! ID: $taskId")
            } catch (e: Exception) {
                Log.e("MainActivity", "Erro ao adicionar task", e)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TaskmanagementappTheme {
        Greeting("Android")
    }
}*/