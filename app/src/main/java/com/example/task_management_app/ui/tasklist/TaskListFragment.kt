package com.example.task_management_app.ui.tasklist

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
        val adapter = TaskListAdapter()
        binding.recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tasks.collectLatest { tasks ->
                adapter.submitList(tasks)
            }
        }

        // Configuração do botão para navegar para o CalendarFragment
        binding.buttonToCalendar.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, CalendarFragment())
                addToBackStack(null)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
