package com.example.tasklyy

import javax.inject.Inject

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
}