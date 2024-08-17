package com.example.task_management_app.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.task_management_app.databinding.FragmentCalendarBinding
import com.example.task_management_app.domain.usecase.getAllDays
import com.example.task_management_app.data.firebase.FirebaseService
import com.example.task_management_app.data.repository.CalendarDayRepositoryImpl
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

        // Inicialização de GetAllDays
        val calendarDayRepository = CalendarDayRepositoryImpl(FirebaseService())
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

        val adapter = CalendarAdapter { day ->
            // Lógica para abrir a visualização das tarefas daquele dia
        }
        binding.calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7) // 7 colunas para os dias da semana
        binding.calendarRecyclerView.adapter = adapter

        // Atualizar a lista de dias quando o mês é alterado
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.days.collectLatest { days ->
                // Atualizar o adapter com os dias destacados
                adapter.submitList(days)
            }
        }

        // Botões para navegar entre os meses
        binding.buttonNextMonth.setOnClickListener {
            viewModel.goToNextMonth()
            refreshDays()
        }

        binding.buttonPreviousMonth.setOnClickListener {
            viewModel.goToPreviousMonth()
            refreshDays()
        }
    }

    // Função para atualizar os dias após alterar o mês
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
