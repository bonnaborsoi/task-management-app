package com.example.task_management_app.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.task_management_app.R
import com.example.task_management_app.databinding.FragmentCalendarBinding
import com.example.task_management_app.domain.usecase.getAllDays
import com.example.task_management_app.data.firebase.FirebaseService
import com.example.task_management_app.data.repository.CalendarDayRepositoryImpl
import com.example.task_management_app.ui.tasklist.TaskListFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CalendarFragment : Fragment() {

    private lateinit var getAllDays: getAllDays

    private val viewModel: CalendarViewModel by viewModels {
        CalendarViewModelFactory(getAllDays)
    }

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseService = FirebaseService()
        val calendarDayRepository = CalendarDayRepositoryImpl(firebaseService)
        getAllDays = getAllDays(calendarDayRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firebaseService = FirebaseService()
        val calendarDayRepository = CalendarDayRepositoryImpl(firebaseService)

        val adapter = CalendarAdapter(
            onDayClicked = { day ->
                //if (day.quantity != -1){
                    // Crie uma instância de TaskListFragment com a data selecionada como argumento
                    val taskListFragment = TaskListFragment().apply {
                        arguments = Bundle().apply {
                            putLong("selectedDate", day.date)
                        }
                    }

                    parentFragmentManager.commit {
                        replace(R.id.fragment_container, taskListFragment)
                        addToBackStack(null)
                    }
                //}
            },
            calendarDayRepository = calendarDayRepository

        )

        binding.calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.calendarRecyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.days.collectLatest { days ->
                adapter.submitList(days)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentMonthName.collectLatest { monthName ->
                binding.monthView.text = monthName
            }
        }

        binding.buttonNextMonth.setOnClickListener {
            viewModel.goToNextMonth()
            refreshDays()
        }

        binding.buttonPreviousMonth.setOnClickListener {
            viewModel.goToPreviousMonth()
            refreshDays()
        }

        binding.buttonToTaskList.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, TaskListFragment())
                addToBackStack(null)
            }
        }
    }

    private fun refreshDays() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.days.collectLatest { days ->
                binding.calendarRecyclerView.adapter?.let {
                    (it as? CalendarAdapter)?.submitList(days)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
