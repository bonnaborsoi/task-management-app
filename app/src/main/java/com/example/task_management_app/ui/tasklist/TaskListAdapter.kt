package com.example.task_management_app.ui.tasklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.task_management_app.R
import com.example.task_management_app.data.model.Task

class TaskListAdapter : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {

private var taskList: List<Task> = emptyList()

override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
    return TaskViewHolder(view)
}

override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
    val task = taskList[position]
    holder.bind(task)
}

override fun getItemCount(): Int {
    return taskList.size
}

fun submitList(tasks: List<Task>) {
    taskList = tasks
    notifyDataSetChanged()
}

class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val taskTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)

    fun bind(task: Task) {
        taskTitle.text = task.name
    }
}
}
