package com.example.tasklyy.Local.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tasklyy.Local.DB.UserEntity

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class UserDatabase: RoomDatabase(){
    abstract val userDao: UserDao
    companion object {

        @Volatile
        private var INSTANCE: UserDatabase? = null


        fun getInstance(context: Context): UserDatabase {
            synchronized(this) {

                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        UserDatabase::class.java,
                        "user_details_database"
                    )
                        .fallbackToDestructiveMigration(false)
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }


}