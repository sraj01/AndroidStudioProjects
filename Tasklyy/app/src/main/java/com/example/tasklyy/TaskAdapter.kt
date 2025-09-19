package com.example.tasklyy

import android.graphics.pdf.models.ListItem
import android.view.MenuItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklyy.databinding.ListItemBinding
import java.util.ArrayList

/*class TaskAdapter(
    private val tasks: List<Task>
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.binding.apply {
            tvTaskTitle.text = task.taskTitle
            tvTaskStatus.text = task.taskStatus
            tvTaskPriority.text = task.taskPriority
        }
    }

    override fun getItemCount() = tasks.size
}
*/

class TaskAdapter(  private val taskList: ArrayList<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder> (){

        override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskViewHolder {
        return TaskViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    class TaskViewHolder(val binding: ListItemBinding): RecyclerView.ViewHolder(binding.root) {}

        override fun onBindViewHolder(
        holder: TaskViewHolder,
        position: Int
    ) {
        val currentItem = taskList[position]
        holder.binding.apply {
          taskTitle.text =currentItem.title
          taskStatus.text =currentItem.status
          taskPriority.text =currentItem.priority
        }
    }
    override fun getItemCount(): Int {
        return taskList.size
    }

}