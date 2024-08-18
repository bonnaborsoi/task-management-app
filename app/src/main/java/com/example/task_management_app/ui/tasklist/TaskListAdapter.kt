package com.example.task_management_app.ui.tasklist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
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

class TaskListAdapter(private val taskRepository: TaskRepositoryImpl) : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {

    private var taskList: List<Task> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view,taskRepository)
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
        taskList = tasks
        notifyDataSetChanged()
    }

    class TaskViewHolder(itemView: View, private val taskRepository: TaskRepositoryImpl) : RecyclerView.ViewHolder(itemView) {
        private val taskTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
        private val taskDueDate: TextView = itemView.findViewById(R.id.tvTaskDueDate)
        private val taskImportance: TextView = itemView.findViewById(R.id.tvTaskImportance)
        private val taskDone: CheckBox = itemView.findViewById(R.id.cbTaskDone)
        //private val taskRepository: TaskRepositoryImpl = TaskRepositoryImpl()

        fun bind(task: Task) {
            taskTitle.text = task.name

            // Converte o tempo em milissegundos para o formato dd/MM/yyyy
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(task.dueDate)
            taskDueDate.text = "Due Date: $formattedDate"

            // Define a importância como "Important" ou "Not Important"
            val importanceText = if (task.markedOnCalendar) "Important" else "Not Important"
            taskImportance.text = "Importance: $importanceText"

            // Define o estado da caixa de seleção como marcada ou não
            taskDone.isChecked = task.completed

            // Lógica para lidar com o clique na caixa de seleção
            taskDone.setOnCheckedChangeListener { _, isChecked ->
                // Atualiza o estado da tarefa aqui, se necessário
                task.completed = isChecked
                // Notifique o ViewModel ou faça a atualização necessária
                CoroutineScope(Dispatchers.IO).launch {
                    val updatedTask = task.copy(id = task.id, completed = isChecked)
                    val success = taskRepository.editTask(updatedTask)
                    withContext(Dispatchers.Main) {
                        if (!success) {
                            // Handle failure, e.g., show a toast or revert the checkbox state
                            taskDone.isChecked = !isChecked
                        }
                    }

                }
            Log.d("TaskViewHolder", "Binding Task: $task")
        }
    }
}
    }
