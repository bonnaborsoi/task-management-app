package com.example.task_management_app.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.task_management_app.R
import com.example.task_management_app.data.model.Day
import java.util.*

class CalendarAdapter(
    private val onDayClicked: (Day) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    private var dayList: List<Day> = emptyList()
    private val currentDayInMillis: Long = System.currentTimeMillis()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        return DayViewHolder(view)
    }

    private fun isCurrentDay(dateInMillis: Long?): Boolean {
        dateInMillis ?: return false
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis

        val today = Calendar.getInstance()
        return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = dayList.getOrNull(position)
        holder.bind(day, onDayClicked)
    }

    override fun getItemCount(): Int = dayList.size

    fun submitList(days: List<Day>) {
        dayList = days
        notifyDataSetChanged()
    }

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayText: TextView = itemView.findViewById(R.id.textViewDay)
        private val viewCircle: View = itemView.findViewById(R.id.viewCircle)

        fun bind(day: Day?, onDayClicked: (Day) -> Unit) {
            day?.let {
                // Extrair o dia do mês a partir do timestamp em milissegundos
                val calendar = Calendar.getInstance().apply { timeInMillis = it.date }
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

                val isCurrentDay = isCurrentDay(it.date)
                val isHighlightedDay = it.quantity > 0

                if (isCurrentDay) {
                    // Destacar o dia atual
                    viewCircle.visibility = View.VISIBLE
                    if (isHighlightedDay) {
                        dayText.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark))
                    } else {
                        dayText.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
                    }
                } else if (isHighlightedDay) {
                    // Destacar o dia que está em 'days'
                    viewCircle.visibility = View.GONE
                    dayText.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark))
                } else {
                    // Dia normal
                    viewCircle.visibility = View.GONE
                    dayText.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.black))
                }

                // Exibir o dia do mês na UI
                dayText.text = dayOfMonth.toString()

                itemView.setOnClickListener { onDayClicked(day) }
            }
        }

        private fun isCurrentDay(dateInMillis: Long): Boolean {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = dateInMillis

            val today = Calendar.getInstance()
            return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
        }
    }
}