package com.example.tasklyy

import androidx.lifecycle.LiveData
import com.example.tasklyy.Local.DB.taskData.Task
import com.example.tasklyy.Local.DB.taskData.TaskDao
import javax.inject.Inject


class TaskRepository @Inject constructor(private val taskDao: TaskDao) {
    suspend fun insertTask(task: Task) = taskDao.insertTask(task)


    fun getTasks(): LiveData<List<Task>> = taskDao.getAllTasks()

}


/*
class TaskRepository @Inject constructor() {
    fun getTasks(): ArrayList<Task> {
        return arrayListOf(
            Task("Splash screen","completed","Low"),
            Task("Login screen","completed","high"),
            Task("SignUp screen","completed","high"),
            Task("DashBoard screen","Inprogress","high"),
            Task(" Calendar screen","completed","medium")
        )

    }
}*/
