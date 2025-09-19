package com.example.tasklyy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
): ViewModel() {

    private val _tasks = MutableLiveData < ArrayList<Task>>()
    val tasks: LiveData <ArrayList<Task>> = _tasks

    init {
         loadTasks()
    }

   private fun loadTasks(){
        _tasks.value = repository.getTasks()
    }
}