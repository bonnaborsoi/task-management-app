package com.example.task_management_app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.task_management_app.data.firebase.FirebaseService
import com.example.task_management_app.data.model.Task
import com.example.task_management_app.data.repository.TaskRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.task_management_app.domain.usecase.CreateTask
import androidx.fragment.app.commit
import com.example.task_management_app.ui.tasklist.TaskListFragment
import com.example.task_management_app.domain.usecase.EditTask
import com.example.task_management_app.domain.usecase.DeleteTask
import com.example.task_management_app.data.repository.CalendarDayRepositoryImpl
import com.example.task_management_app.ui.map.MapViewFragment
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var createTask: CreateTask
    private lateinit var editTask: EditTask
    private lateinit var deleteTask: DeleteTask
    private lateinit var dayRepository: CalendarDayRepositoryImpl
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // VERIFICAR SE FUNCIONA!!!!
        val firebaseService = FirebaseService()
        val calendarDayRepository = CalendarDayRepositoryImpl(firebaseService)
        val taskRepository = TaskRepositoryImpl(firebaseService, calendarDayRepository)
        createTask = CreateTask(taskRepository)
        editTask = EditTask(taskRepository)
        deleteTask = DeleteTask(taskRepository)

        // Define uma função para obter o início do dia
        fun getStartOfDay(timestamp: Long): Long {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = timestamp
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return calendar.timeInMillis
        }

// Cria uma nova task e a adiciona ao Firebase
        val currentTimestamp = System.currentTimeMillis()
        val startOfDayTimestamp = getStartOfDay(currentTimestamp) // Ajusta para o início do dia

        val newTask = Task(
            name = "Lucca",
            dueDate = startOfDayTimestamp, // Usa o timestamp ajustado
            completed = false,
            markedOnCalendar = true
        )
        // Cria uma nova task e a adiciona ao Firebase
        /*val newTask = Task(
            id = null,
            name = "Lucca",
            dueDate = System.currentTimeMillis(),
            completed = false,
            markedOnCalendar = true
        )*/

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val taskId = createTask(newTask) // Usando o caso de uso CreateTask
                Log.d("MainActivity", "Task adicionada com sucesso! ID: $taskId")
            } catch (e: Exception) {
                Log.e("MainActivity", "Erro ao adicionar task", e)
            }
        }
        /*
        val id_delete : String = "-O4EEax5J_Ppp_3jy2yq"
        CoroutineScope(Dispatchers.IO).launch {
            try {

                // Deleta a task recém-criada
                val deleteSuccess = deleteTask(id_delete)
                if (deleteSuccess) {
                    Log.d("MainActivity", "Task deletada com sucesso! ID: $id_delete")
                } else {
                    Log.e("MainActivity", "Falha ao deletar a task com ID: $id_delete")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Erro ao adicionar ou deletar task", e)
            }
        }*/
        /*
        dayRepository = CalendarDayRepositoryImpl(firebaseService)

        // Teste de createDay
        //val testDay: Long = System.currentTimeMillis()
        val testDay: Long = 1723606085057
        val testDay2: Long = 1723606187900
        // Teste de incrementDay
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val isDayIncremented = dayRepository.incrementDay(testDay)
                if (isDayIncremented) {
                    Log.d("MainActivity", "Dia incrementado com sucesso: $testDay")
                } else {
                    Log.e("MainActivity", "Erro ao incrementar o dia: $testDay")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Exceção ao incrementar o dia", e)
            }

            // Teste de getAllDays
            try {
                dayRepository.getAllDays().collect { days ->
                    Log.d("MainActivity", "Dias recuperados: $days")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Erro ao recuperar dias", e)
            }

            // Teste de decrementDay
            try {
                val isDayDecremented = dayRepository.decrementDay(testDay2)
                if (isDayDecremented) {
                    Log.d("MainActivity", "Dia decrementado com sucesso: $testDay2")
                } else {
                    Log.e("MainActivity", "Erro ao decrementar o dia: $testDay2")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Exceção ao decrementar o dia", e)
            }

            // Recupera os dias novamente após a modificação para confirmar
            try {
                dayRepository.getAllDays().collect { days ->
                    Log.d("MainActivity", "Dias após a modificação: $days")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Erro ao recuperar dias após modificação", e)
            }
        }*/
        /*editar task
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Adiciona uma nova task ao Firebase
                val taskId = createTask(newTask)
                Log.d("MainActivity", "Task adicionada com sucesso! ID: $taskId")

                // Simula a edição da task recém-criada
                val updatedTask = newTask.copy(id = taskId, name = "Suki - Editado")
                val success = editTask(updatedTask)

                if (success) {
                    Log.d("MainActivity", "Task editada com sucesso!")
                } else {
                    Log.e("MainActivity", "Falha ao editar a task")
                }

            } catch (e: Exception) {
                Log.e("MainActivity", "Erro ao adicionar ou editar task", e)
            }
        }*/
        /*deletar task
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Adiciona uma nova task
                val taskId = createTask(newTask)
                Log.d("MainActivity", "Task adicionada com sucesso! ID: $taskId")

                // Deleta a task recém-criada
                val deleteSuccess = deleteTask(taskId)
                if (deleteSuccess) {
                    Log.d("MainActivity", "Task deletada com sucesso! ID: $taskId")
                } else {
                    Log.e("MainActivity", "Falha ao deletar a task com ID: $taskId")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Erro ao adicionar ou deletar task", e)
            }
        }*/
        /*
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, TaskListFragment())
            }
        }*/
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, TaskListFragment() /*CalendarFragment()*/)
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
            Completed = false,
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