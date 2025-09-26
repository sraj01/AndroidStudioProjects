package com.example.tasklyy

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklyy.Local.DB.taskData.Task
import com.example.tasklyy.databinding.ListItemBinding
import java.util.Locale

class TaskAdapter(private var originalList: List<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(), Filterable {

    private var filteredList: MutableList<Task> = originalList.toMutableList()

    inner class TaskViewHolder(val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = filteredList[position]
        holder.binding.apply {
            taskTitle.text = currentTask.title
            taskPriority.text = when (currentTask.taskPriority) {
                0 -> "High"
                1 -> "Medium"
                2 -> "Low"
                else -> "Unknown"
            }
        }
    }

    override fun getItemCount(): Int = filteredList.size

    fun updateList(newList: List<Task>) {
        originalList = newList
        filteredList = newList.toMutableList() // reset filtered list
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase(Locale.ROOT)?.trim() ?: ""
                val resultList = if (query.isEmpty()) {
                    originalList
                } else {
                    originalList.filter { it.title.lowercase(Locale.ROOT).contains(query) }
                }
                val filterResults = FilterResults()
                filterResults.values = resultList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as? List<Task>)?.toMutableList() ?: mutableListOf()
                notifyDataSetChanged()
            }
        }
    }
}
