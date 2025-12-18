package com.example.to_doapp.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.to_doapp.utils.Constants
import java.util.Date

@Entity(Constants.Companion.TASK)
data class Task(
    @PrimaryKey(autoGenerate = true) val sl_id: Int = 0,
    val task_name: String,
    val _date: Date? = null,
    val iscompleted: Boolean,
    val isStarred:Boolean=false
)