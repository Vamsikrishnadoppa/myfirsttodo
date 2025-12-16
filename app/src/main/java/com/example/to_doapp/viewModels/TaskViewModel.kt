package com.example.to_doapp.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_doapp.db.model.Task
import com.example.to_doapp.db.TaskDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskdata = TaskDatabase.Companion.getDatabase(application).taskDao()
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
    private val _selectedDate = MutableStateFlow(Date())   // default = today
    val selectedDate: StateFlow<Date> = _selectedDate

    //
    fun loadTasks() {
        viewModelScope.launch {
            _tasks.value = taskdata.getAllTasks() }
    }
    fun tasksforselecteddate() {

        viewModelScope.launch {
            val cal = Calendar.getInstance()
            cal.time = selectedDate.value

            // Start of day
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val start = cal.time

            // End of day
            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)
            cal.set(Calendar.MILLISECOND, 999)
            val end = cal.time
             //_tasks.value = taskdata.getselectedTasks(start, end)

            val selectedTasks = taskdata.getselectedTasks(start, end)
            _tasks.value = selectedTasks.sortedBy { it.iscompleted }
        }
    }

    fun getDateValue(): String {
        return SimpleDateFormat("dd", Locale.getDefault())
            .format(selectedDate.value)
    }

    fun getMonthValue(): String {
        return SimpleDateFormat("MMM", Locale.getDefault()).format(
            selectedDate.value
        )
    }

    fun getYearValue(): String {
        return SimpleDateFormat("yyyy", Locale.getDefault()).format(
            selectedDate.value
        )
    }

    fun updateSelectedDate(date: Date){
        _selectedDate.value=date
    }

    /* sample commit in UAT-2
     */
    fun insertTask(task_name: String) {
        viewModelScope.launch {
            val newTask = Task(
                task_name = task_name,
                _date = selectedDate.value,
                iscompleted = false
            )
            taskdata.insertTask(newTask)
            //loadTasks()
            tasksforselecteddate()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskdata.deleteTask(task)
            tasksforselecteddate()
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskdata.updateTask(task)
            tasksforselecteddate()
        }
    }


}