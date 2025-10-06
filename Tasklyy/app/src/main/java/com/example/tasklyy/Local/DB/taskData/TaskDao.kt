package com.example.tasklyy.Local.DB.taskData

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import android.util.Log
import androidx.room.Delete
import androidx.room.Update


@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE task_status = :status ORDER BY task_priority ASC, task_due_date ASC")
    fun getTasksByStatus(status: String): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE task_due_date >= :todayStart AND task_due_date <= :todayEnd ORDER BY task_due_date ASC")
    fun getTasksDueToday(todayStart: Long, todayEnd: Long): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE assigneeId = :userIdOfAssignee ORDER BY task_priority ASC, task_due_date ASC")
    fun getTasksByAssignee(userIdOfAssignee: Int): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Int): LiveData<Task?>

    @Update // Add this for updating tasks
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    /*@Query("SELECT * FROM tasks WHERE task_status = :taskStatus = 'todo' ")
    fun getTasksByInProgress(taskStatus: String): List<Task>

    @Query("SELECT * FROM tasks WHERE task_status = :taskStatus ")
    fun getTasksByDone(taskStatus: String): List<Task>*/
}