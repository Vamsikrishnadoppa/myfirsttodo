package com.example.to_doapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity("Tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val sl_id: Int=0 ,
    val task_name: String,
   val _date: Date? = null,
    val iscompleted: Boolean

    )
