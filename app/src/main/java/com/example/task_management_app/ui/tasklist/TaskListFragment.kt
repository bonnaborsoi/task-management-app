package com.example.task_management_app.ui.tasklist

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.task_management_app.R
import com.example.task_management_app.databinding.FragmentTaskListBinding
import com.example.task_management_app.domain.usecase.GetAllTasks
import com.example.task_management_app.data.firebase.FirebaseService
import com.example.task_management_app.data.model.Task
import com.example.task_management_app.data.repository.TaskRepositoryImpl
import com.example.task_management_app.data.repository.CalendarDayRepositoryImpl
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.task_management_app.ui.calendar.CalendarFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*
import com.example.task_management_app.domain.usecase.CreateTask
import com.example.task_management_app.ui.map.MapViewFragment

class TaskListFragment : Fragment() {

    private lateinit var getAllTasks: GetAllTasks
    private val viewModel: TaskListViewModel by viewModels {
        TaskListViewModelFactory(getAllTasks)
    }
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private var completedFilter: String = "All tasks"
    private var importantFilter: String = "All Tasks"
    private lateinit var createTask: CreateTask


    lateinit var adapter: TaskListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val taskRepository = TaskRepositoryImpl(FirebaseService(), CalendarDayRepositoryImpl(FirebaseService()))
        getAllTasks = GetAllTasks(taskRepository)

        arguments?.getLong("selectedDate")?.let { selectedDate ->
            viewModel.filterTasksByDate(selectedDate)
        }
        createTask = CreateTask(taskRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val taskRepository = TaskRepositoryImpl(FirebaseService(), CalendarDayRepositoryImpl(FirebaseService()))
        adapter = TaskListAdapter(taskRepository,
            onTaskRemoved = { task ->
                adapter.removeTask(task)
                parentFragmentManager.commit {
                    replace(R.id.fragment_container, TaskListFragment())
                    addToBackStack(null)
                }
            },
            onTaskEdited = { task ->
                adapter.editTask(task)
                parentFragmentManager.commit {
                    replace(R.id.fragment_container, TaskListFragment())
                    addToBackStack(null)
                }
            },
            onLocationClicked = { task ->
                Log.d("FragmentTask: ", "Reached here")
                val taskId = task.id
                val taskLocation = task.location
                parentFragmentManager.commit {
                    replace(R.id.fragment_container, MapViewFragment.newInstance(taskId, taskLocation))
                    addToBackStack(null)
                }
            }
        )

        binding.recyclerView.adapter = adapter

        val completedSpinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.completed_filter_options,
            R.layout.spinner_item
        )
        completedSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFilterCompleted.adapter = completedSpinnerAdapter

        binding.spinnerFilterCompleted.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                completedFilter = parent.getItemAtPosition(position) as String
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val importantSpinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.important_filter_options,
            R.layout.spinner_item
        )
        importantSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFilterImportant.adapter = importantSpinnerAdapter

        binding.spinnerFilterImportant.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                importantFilter = parent.getItemAtPosition(position) as String
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tasks.collectLatest { tasks ->
                adapter.submitList(tasks)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filterDate.collectLatest { date ->
                binding.buttonClearFilter.visibility = if (date != null) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }

        binding.buttonFilter.setOnClickListener {
            showDatePicker { selectedDate ->
                viewModel.filterTasksByDate(selectedDate)
            }
        }

        binding.buttonClearFilter.setOnClickListener {
            viewModel.clearFilter()
        }

        binding.composeViewButtonToCalendar.apply {
            setContent {
                NavigationButtonToCalendar { navigateToCalendarFragment() }
            }
        }
        binding.composeAddTaskButton.apply {
            setContent {
                AddTaskButton { AddTaskFunction() }
            }
        }
    }

    private fun applyFilters(tasks: List<Task> = viewModel.tasks.value) {
        var filteredTasks = tasks

        filteredTasks = when (completedFilter) {
            "Completed tasks Only" -> filteredTasks.filter { it.completed }
            "Non-Completed tasks Only" -> filteredTasks.filter { !it.completed }
            else -> filteredTasks
        }

        filteredTasks = when (importantFilter) {
            "Important Only" -> filteredTasks.filter { it.markedOnCalendar }
            "Non-Important Only" -> filteredTasks.filter { !it.markedOnCalendar }
            else -> filteredTasks
        }

        adapter.submitList(filteredTasks)
    }

    private fun showDatePicker(onDateSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth, 0, 0, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                onDateSelected(calendar.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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

    @Composable
    fun NavigationButtonToCalendar(onClick: () -> Unit) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF071952),
                contentColor = Color.White
            ),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Go to Calendar")
        }
    }

    private fun navigateToCalendarFragment() {
        parentFragmentManager.commit {
            replace(R.id.fragment_container, CalendarFragment())
            addToBackStack(null)
        }
    }

    @Composable
    fun AddTaskButton(onClick: () -> Unit) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF071952),
                contentColor = Color.White
            ),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Add Task")
        }
    }



    private fun AddTaskFunction() {
        val currentTimestamp = System.currentTimeMillis()
        val startOfDayTimestamp = getStartOfDay(currentTimestamp) // Ajusta para o início do dia

        val newTask = Task(
            name = "New Task",
            dueDate = startOfDayTimestamp, // Usa o timestamp ajustado
            completed = false,
            markedOnCalendar = false,
            location = "None"
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val taskId = createTask(newTask) // Usando o caso de uso CreateTask
                Log.d("MainActivity", "Task adicionada com sucesso! ID: $taskId")
                parentFragmentManager.commit {
                    replace(R.id.fragment_container, TaskListFragment())
                    addToBackStack(null)
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Erro ao adicionar task", e)
            }
        }
    }
}
