package com.example.tasklyy.Local.DB.taskData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.tasklyy.Local.DB.UserEntity
import com.google.type.Date

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["assigneeId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("assigneeId")]
)

data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "task_title") val title: String,
    @ColumnInfo(name = "task_description") val description: String,
    @ColumnInfo(name = "task_priority") val taskPriority: Int,
    @ColumnInfo(name = "task_due_date") val dueDate: String,
    @ColumnInfo(name = "task_status") val taskStatus: String,
    val assigneeId: Int?,
    @ColumnInfo(name = "location_address") val locationAddress: String?


    /*    val title: String,
    val description: String,
    val priority: Int,         // 0=High, 1=Medium, 2=Low
    val assigneeId: Int?,      // User Id
    val dueDate: Long,         // Timestamp
    val status: Int            // 0=Todo, 1=InProgress, 2=Done
)
*/

)