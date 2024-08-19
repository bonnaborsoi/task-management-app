package com.example.task_management_app.ui.tasklist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.task_management_app.R
import com.example.task_management_app.data.model.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.task_management_app.data.repository.TaskRepositoryImpl

class TaskListAdapter(
    private val taskRepository: TaskRepositoryImpl,
    private val onTaskRemoved: (Task) -> Unit // Adicione um callback para remover a tarefa
) : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {

    private var taskList: MutableList<Task> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view, taskRepository, onTaskRemoved)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.bind(task)
        Log.d("TaskListAdapter", "Task at position $position: $task")
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun submitList(tasks: List<Task>) {
        taskList = tasks.toMutableList()
        notifyDataSetChanged()
    }

    fun removeTask(task: Task) {
        val position = taskList.indexOf(task)
        if (position != -1) {
            taskList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    class TaskViewHolder(
        itemView: View,
        private val taskRepository: TaskRepositoryImpl,
        private val onTaskRemoved: (Task) -> Unit // Adicione o callback no ViewHolder
    ) : RecyclerView.ViewHolder(itemView) {
        private val taskTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
        private val taskDueDate: TextView = itemView.findViewById(R.id.tvTaskDueDate)
        private val taskImportance: TextView = itemView.findViewById(R.id.tvTaskImportance)
        private val taskDone: CheckBox = itemView.findViewById(R.id.cbTaskDone)
        private val deleteThisTask: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(task: Task) {
            taskTitle.text = task.name
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(task.dueDate)
            taskDueDate.text = "Due Date: $formattedDate"
            val importanceText = if (task.markedOnCalendar) "Important" else "Not Important"
            taskImportance.text = "Importance: $importanceText"
            taskDone.isChecked = task.completed

            taskDone.setOnCheckedChangeListener { _, isChecked ->
                task.completed = isChecked
                CoroutineScope(Dispatchers.IO).launch {
                    val updatedTask = task.copy(id = task.id, completed = isChecked)
                    val success = taskRepository.editTask(updatedTask)
                    withContext(Dispatchers.Main) {
                        if (!success) {
                            taskDone.isChecked = !isChecked
                        }
                    }
                }
                Log.d("TaskViewHolder", "Binding Task: $task")
            }

            deleteThisTask.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val updatedTask = task.copy(id = task.id)
                    val success = taskRepository.deleteTask(updatedTask)
                    withContext(Dispatchers.Main) {
                        if (success) {
                            onTaskRemoved(task) // Chama o callback para remover a tarefa
                        } else {
                            Log.d("ERROR:", "Task not deleted")
                        }
                    }
                }
            }
        }
    }
}
