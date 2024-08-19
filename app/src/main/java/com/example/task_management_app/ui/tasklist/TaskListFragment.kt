package com.example.task_management_app.ui.tasklist

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.task_management_app.R
import com.example.task_management_app.databinding.FragmentTaskListBinding
import com.example.task_management_app.domain.usecase.GetAllTasks
import com.example.task_management_app.data.firebase.FirebaseService
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tasks.collectLatest { tasks ->
                adapter.submitList(tasks)
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
