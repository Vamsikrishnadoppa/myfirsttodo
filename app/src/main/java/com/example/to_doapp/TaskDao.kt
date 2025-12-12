package com.example.to_doapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.Date


@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

     @Delete
     suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM Tasks")
    suspend fun getAllTasks(): List<Task>

@Query("SELECT * FROM TASKS WHERE _date BETWEEN :start AND :end")
suspend fun getselectedTasks(start: Date,end: Date):List<Task>

}