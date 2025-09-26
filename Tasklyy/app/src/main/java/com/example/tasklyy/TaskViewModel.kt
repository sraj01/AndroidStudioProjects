package com.example.tasklyy

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasklyy.Local.DB.UserDao
import com.example.tasklyy.Local.DB.UserEntity
import com.example.tasklyy.Local.DB.taskData.Task
import com.example.tasklyy.Local.DB.taskData.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val userDao: UserDao,
    private val taskDao: TaskDao,
    private val taskRepository: TaskRepository
) : ViewModel() {



    val users: LiveData<List<UserEntity>> = userDao.getAllUsers()

    val task: LiveData<List<Task>> = taskDao.getAllTasks()

    val allTasks: LiveData<List<Task>> = taskRepository.getTasks()

    fun saveTask(task: Task) {
        viewModelScope.launch {
            taskRepository.insertTask(task)
        }
    }

/*
    fun getTask(task: Task?) {
        viewModelScope.launch {
            taskRepository.getAllTasks()
        }
    }
*/
}

