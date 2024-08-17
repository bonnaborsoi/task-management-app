package com.example.task_management_app.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.task_management_app.R
import com.example.task_management_app.data.model.Day
import java.util.*

class CalendarAdapter(
    private val onDayClicked: (Day) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    private var dayList: List<Day> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = dayList.getOrNull(position)
        holder.bind(day, onDayClicked)
    }

    override fun getItemCount(): Int = 42 // Exibir 6 semanas, mesmo que algumas células fiquem vazias

    fun submitList(days: List<Day>) {
        dayList = days
        notifyDataSetChanged()
    }

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayText: TextView = itemView.findViewById(R.id.textViewDay) // conferir

        fun bind(day: Day?, onDayClicked: (Day) -> Unit) {
            day?.let {
                dayText.text = it.date.toString() // Conversão do timestamp para dia
                //itemView.setOnClickListener { onDayClicked(it) } // AJEITARRR

                // Lógica para destacar o dia
                if (it.quantity > 0) {
                    itemView.setBackgroundResource(R.drawable.highlight_background) // conferir
                } else {
                    itemView.setBackgroundResource(0)
                }
            }
        }

    }
}