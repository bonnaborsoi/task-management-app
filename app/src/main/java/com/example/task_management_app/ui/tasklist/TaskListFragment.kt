package com.example.task_management_app.ui.tasklist

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import java.util.*

class TaskListFragment : Fragment() {

    private lateinit var getAllTasks: GetAllTasks
    private val viewModel: TaskListViewModel by viewModels {
        TaskListViewModelFactory(getAllTasks)
    }
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    // Variáveis para armazenar o estado dos filtros
    private var completedFilter: String = "All tasks"
    private var importantFilter: String = "All Tasks"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialização de GetAllTasks
        val taskRepository = TaskRepositoryImpl(FirebaseService(), CalendarDayRepositoryImpl(FirebaseService()))
        getAllTasks = GetAllTasks(taskRepository)

        // Verifica se há uma data selecionada para filtrar as tarefas
        arguments?.getLong("selectedDate")?.let { selectedDate ->
            viewModel.filterTasksByDate(selectedDate)
        }
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
        val adapter = TaskListAdapter(taskRepository)
        binding.recyclerView.adapter = adapter

        // Configurar o Spinner para o filtro de completados
        val completedSpinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.completed_filter_options,
            R.layout.spinner_item
            //android.R.layout.simple_spinner_item
        )
        completedSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFilterCompleted.adapter = completedSpinnerAdapter

        binding.spinnerFilterCompleted.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                completedFilter = parent.getItemAtPosition(position) as String
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Não é necessário fazer nada
            }
        }

        // Configurar o Spinner para o filtro de importância
        val importantSpinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.important_filter_options,
            R.layout.spinner_item
            //android.R.layout.simple_spinner_item
        )
        importantSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFilterImportant.adapter = importantSpinnerAdapter

        binding.spinnerFilterImportant.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                importantFilter = parent.getItemAtPosition(position) as String
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Não é necessário fazer nada
            }
        }

        // Coletar as tarefas do ViewModel e aplicar os filtros
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tasks.collectLatest { tasks ->
                applyFilters(tasks)
            }
        }

        // Configuração do botão de filtro
        binding.buttonFilter.setOnClickListener {
            showDatePicker { selectedDate ->
                viewModel.filterTasksByDate(selectedDate)
            }
        }

        // Configuração do botão para desfazer o filtro
        binding.buttonClearFilter.setOnClickListener {
            viewModel.clearFilter()
        }

        // Configuração do botão para navegar para o CalendarFragment
        binding.buttonToCalendar.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, CalendarFragment())
                addToBackStack(null)
            }
        }
    }

    private fun applyFilters(tasks: List<Task> = viewModel.tasks.value) {
        var filteredTasks = tasks

        // Aplicar filtro por completados
        filteredTasks = when (completedFilter) {
            "Completed tasks Only" -> filteredTasks.filter { it.completed }
            "Non-Completed tasks Only" -> filteredTasks.filter { !it.completed }
            else -> filteredTasks
        }

        // Aplicar filtro por importância
        filteredTasks = when (importantFilter) {
            "Important Only" -> filteredTasks.filter { it.markedOnCalendar }
            "Non-Important Only" -> filteredTasks.filter { !it.markedOnCalendar }
            else -> filteredTasks
        }

        (binding.recyclerView.adapter as TaskListAdapter).submitList(filteredTasks)
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
}
