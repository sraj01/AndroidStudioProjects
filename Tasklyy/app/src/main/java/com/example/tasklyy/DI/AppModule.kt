package com.example.tasklyy.DI

import android.content.Context
import androidx.room.Room
import com.example.tasklyy.Local.Preference.SharedPrefsHelper
import com.example.tasklyy.Local.DB.UserDao
import com.example.tasklyy.Local.DB.AppDatabase
import com.example.tasklyy.Local.DB.taskData.TaskDao
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "tasklyy_db"
        ).build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao
    }

    @Provides
    @Singleton
    fun provideSharedPrefsHelper(@ApplicationContext appContext: Context): SharedPrefsHelper {
        return SharedPrefsHelper(appContext)
    }
    @Provides fun provideTaskDao(database: AppDatabase): TaskDao{
        return database.taskDao
    }

}