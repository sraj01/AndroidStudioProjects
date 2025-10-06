package com.example.tasklyy.Local.DB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tasklyy.Local.DB.UserEntity

@Dao
interface UserDao {

    @Insert
    suspend fun insert(register: UserEntity)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users ORDER BY userId DESC")
    fun getAllUsers(): LiveData<List<UserEntity>>

    @Query("SELECT * FROM users WHERE user_name LIKE :userName")
    suspend fun getUserfromUsername(userName: String): UserEntity?

    @Query("SELECT * FROM users WHERE userId = :id")
    fun getUserById(id: Int): LiveData<UserEntity?>

    @Query("SELECT * FROM users WHERE email_id = :emailId LIMIT 1")
    suspend fun getUserfromEmail(emailId: String): UserEntity?

}