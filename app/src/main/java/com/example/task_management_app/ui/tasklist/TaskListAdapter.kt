package com.example.task_management_app.ui.tasklist

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.task_management_app.R
import com.example.task_management_app.data.model.Day
import com.example.task_management_app.data.model.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.task_management_app.data.repository.TaskRepositoryImpl
import com.example.task_management_app.ui.map.MapViewFragment
import com.google.android.gms.maps.MapFragment
import java.util.Calendar

class TaskListAdapter(
    private val taskRepository: TaskRepositoryImpl,
    private val onTaskRemoved: (Task) -> Unit,
    private val onTaskEdited: (Task) -> Unit,
    private val onLocationClicked: (Task) -> Unit
) : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {

    private var taskList: MutableList<Task> = mutableListOf()
    private var isEditing: Boolean = false  // Variável global de edição

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view, taskRepository, onTaskRemoved, onTaskEdited, ::isEditingFun, ::setEditingState, onLocationClicked)
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

    fun editTask(task: Task) {
        val position = taskList.indexOfFirst { it.id == task.id }
        if (position != -1) {
            taskList[position] = task
            notifyItemChanged(position)
        }
    }

    private fun isEditingFun(): Boolean = isEditing

    private fun setEditingState(editing: Boolean) {
        isEditing = editing
    }

    class TaskViewHolder(
        itemView: View,
        private val taskRepository: TaskRepositoryImpl,
        private val onTaskRemoved: (Task) -> Unit,
        private val onTaskEdited: (Task) -> Unit,
        private val isEditing: () -> Boolean,
        private val setEditingState: (Boolean) -> Unit,
        private val onLocationClicked: (Task) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val taskTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
        private val taskDueDate: TextView = itemView.findViewById(R.id.tvTaskDueDate)
        private val taskImportance: TextView = itemView.findViewById(R.id.tvTaskImportance)
        private val taskDone: CheckBox = itemView.findViewById(R.id.cbTaskDone)
        private val deleteThisTask: ImageButton = itemView.findViewById(R.id.btnDelete)
        private val editThisTask: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val taskLocation: TextView = itemView.findViewById(R.id.tvTaskLocation)

        fun bind(task: Task) {
            taskTitle.text = task.name
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(task.dueDate)
            taskDueDate.text = "Due Date: $formattedDate"
            val importanceText = if (task.markedOnCalendar) "Important" else "Not Important"
            taskImportance.text = "Importance: $importanceText"
            taskDone.isChecked = task.completed
            val locationText = task.location
            taskLocation.text = "Location: $locationText"

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

            taskLocation.setOnClickListener {
                Log.d("taskLocation: ","Clicked")
                if (task.location != "None") {
                    onLocationClicked(task)
                }
            }


            deleteThisTask.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val updatedTask = task.copy(id = task.id)
                    val success = taskRepository.deleteTask(updatedTask)
                    withContext(Dispatchers.Main) {
                        if (success) {
                            onTaskRemoved(task)
                        } else {
                            Log.d("ERROR:", "Task not deleted")
                        }
                    }
                }
            }

            editThisTask.setOnClickListener {
                if (isEditing()) return@setOnClickListener

                setEditingState(true)

                val dialogView = LayoutInflater.from(itemView.context).inflate(R.layout.dialog_edit_task, null)
                val etTaskName = dialogView.findViewById<EditText>(R.id.etTaskName)
                val tvTaskDate = dialogView.findViewById<TextView>(R.id.tvTaskDate)
                val cbTaskImportance = dialogView.findViewById<CheckBox>(R.id.cbTaskImportance)
                val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirm)
                val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
                val etTaskLocation = dialogView.findViewById<EditText>(R.id.etTaskLocation)

                etTaskName.setText(task.name)
                tvTaskDate.text = dateFormat.format(task.dueDate)
                cbTaskImportance.isChecked = task.markedOnCalendar
                etTaskLocation.setText(task.location)

                tvTaskDate.setOnClickListener {
                    val calendar = Calendar.getInstance()
                    val datePickerDialog = DatePickerDialog(
                        itemView.context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(year, month, dayOfMonth, 0, 0, 0)
                            calendar.set(Calendar.MILLISECOND, 0)
                            task.dueDate = calendar.timeInMillis
                            tvTaskDate.text = dateFormat.format(task.dueDate)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    datePickerDialog.show()
                }

                val alertDialog = AlertDialog.Builder(itemView.context)
                    .setView(dialogView)
                    .setCancelable(false)
                    .create()

                btnConfirm.setOnClickListener {
                    val updatedTask = task.copy(
                        name = etTaskName.text.toString(),
                        dueDate = task.dueDate,
                        markedOnCalendar = cbTaskImportance.isChecked,
                        location = etTaskLocation.text.toString()
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        val success = taskRepository.editTask(updatedTask)
                        withContext(Dispatchers.Main) {
                            if (success) {
                                bind(updatedTask)
                                onTaskEdited(task)
                                alertDialog.dismiss()
                            } else {
                                Toast.makeText(itemView.context, "Failed to update task", Toast.LENGTH_SHORT).show()
                            }
                            setEditingState(false)
                        }
                    }
                }

                btnCancel.setOnClickListener {
                    alertDialog.dismiss()
                    setEditingState(false)
                }

                alertDialog.show()
            }
        }
    }
}
