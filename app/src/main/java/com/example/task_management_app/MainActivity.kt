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


        val firebaseService = FirebaseService()
        val calendarDayRepository = CalendarDayRepositoryImpl(firebaseService)
        val taskRepository = TaskRepositoryImpl(firebaseService, calendarDayRepository)
        createTask = CreateTask(taskRepository)
        editTask = EditTask(taskRepository)
        deleteTask = DeleteTask(taskRepository)
        
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, TaskListFragment() /*CalendarFragment()*/)
            }
        }
    }
}