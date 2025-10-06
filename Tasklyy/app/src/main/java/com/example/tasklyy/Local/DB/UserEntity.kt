package com.example.tasklyy.Local.DB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Users")
data class UserEntity(

    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,


    @ColumnInfo(name = "user_name")
    var userName: String = "",

    @ColumnInfo(name = "password")
    var passWord: String = "",

    @ColumnInfo(name = "first_name")
    var firstName: String = "",

    @ColumnInfo(name = "last_name")
    var lastName: String = "",
    @ColumnInfo(name = "email_id")
    var emailId: String = "",

    @ColumnInfo(name = "profile_pic")
    var profilePic: String = "",

    )