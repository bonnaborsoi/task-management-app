package com.example.task_management_app.ui.calendar

import android.util.Log
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
                val calendar = Calendar.getInstance().apply { timeInMillis = it.date }
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

                val isCurrentDay = isCurrentDay(it.date)
                val isHighlightedDay = it.quantity > 0

                // Adicionando logs para depuração
                Log.d("CalendarAdapter", "Bind ViewHolder - Day: $dayOfMonth, DateInMillis: ${it.date}, IsCurrentDay: $isCurrentDay, IsHighlightedDay: $isHighlightedDay")

                if (isCurrentDay) {
                    viewCircle.visibility = View.VISIBLE
                    dayText.setTextColor(
                        if (isHighlightedDay) {
                            ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark)
                        } else {
                            ContextCompat.getColor(itemView.context, android.R.color.white)
                        }
                    )
                } else {
                    viewCircle.visibility = View.GONE
                    dayText.setTextColor(
                        if (isHighlightedDay) {
                            ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark)
                        } else {
                            ContextCompat.getColor(itemView.context, android.R.color.black)
                        }
                    )
                }

                dayText.text = dayOfMonth.toString()
                itemView.setOnClickListener { onDayClicked(day) }
            }
        }

        private fun isCurrentDay(dateInMillis: Long): Boolean {
            val calendar = Calendar.getInstance().apply { timeInMillis = dateInMillis }
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val isCurrent = calendar.timeInMillis == today.timeInMillis

            // Adicionando log para depuração
            Log.d("CalendarAdapter", "Is Current Day - DateInMillis: $dateInMillis, Result: $isCurrent")

            return isCurrent
        }
    }
}