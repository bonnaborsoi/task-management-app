package com.example.task_management_app.ui.tasklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.task_management_app.data.repository.CalendarDayRepositoryImpl
import com.example.task_management_app.ui.calendar.CalendarFragment

class TaskListFragment : Fragment() {

    private lateinit var getAllTasks: GetAllTasks
    private val viewModel: TaskListViewModel by viewModels {
        TaskListViewModelFactory(getAllTasks)
    }
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    // Variáveis para armazenar o estado dos filtros
    private var showCompleted: Boolean = false
    private var showImportant: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val taskRepository = TaskRepositoryImpl(FirebaseService(), CalendarDayRepositoryImpl(FirebaseService()))
        getAllTasks = GetAllTasks(taskRepository)
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
        // Passa o TaskRepositoryImpl para o adaptador
        val taskRepository = TaskRepositoryImpl(FirebaseService(), CalendarDayRepositoryImpl(FirebaseService()))
        val adapter = TaskListAdapter(taskRepository)
        //val adapter = TaskListAdapter(getAllTasks.taskRepository)
       // val adapter = TaskListAdapter(taskRepository)
        binding.recyclerView.adapter = adapter

        // Inicializar filtros
        binding.cbFilterCompleted.setOnCheckedChangeListener { _, isChecked ->
            showCompleted = isChecked
            applyFilters()
        }

        binding.cbFilterImportant.setOnCheckedChangeListener { _, isChecked ->
            showImportant = isChecked
            applyFilters()
        }

        // Coletar as tarefas do ViewModel e aplicar os filtros
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tasks.collectLatest { tasks ->
                applyFilters(tasks)
            }
        }

        binding.buttonToCalendar.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, CalendarFragment())
                addToBackStack(null)
            }
        }
    }

    private fun applyFilters(tasks: List<Task> = viewModel.tasks.value) {
        var filteredTasks = tasks

        // Aplicar filtro por completado
        if (showCompleted) {
            filteredTasks = filteredTasks.filter { it.completed }
        } else {
            // Se não for para mostrar completadas, mostrar todas as tarefas, completadas e não completadas
            filteredTasks = filteredTasks.filter { !it.completed }
        }

        // Aplicar filtro por importância
        if (showImportant) {
            filteredTasks = filteredTasks.filter { it.markedOnCalendar }
        }

        (binding.recyclerView.adapter as TaskListAdapter).submitList(filteredTasks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
