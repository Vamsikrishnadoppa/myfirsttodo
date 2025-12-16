package com.example.to_doapp.ui.views

import android.widget.ScrollView
import androidx.compose.animation.core.copy
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.room.util.TableInfo
import com.example.to_doapp.db.model.Task
import com.example.to_doapp.viewModels.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.List

@Composable
fun TaskHomeScreen(
    modifier: Modifier = Modifier,navController: NavHostController ,viewModel: TaskViewModel
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val taskList by viewModel.tasks.collectAsState() //observe tasks from viewmodel
    var isLoading by remember { mutableStateOf(true )}

    var showDatePicker by remember { mutableStateOf(false) }
    LaunchedEffect(selectedDate) { // Load tasks once when screen opens
        isLoading=true
        viewModel.tasksforselecteddate()
        kotlinx.coroutines.delay(500L)
        isLoading=false
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)) {

        Column {
            TopContentView(
                modifier = modifier,
               // viewModel = viewModel,
                currentDate = selectedDate,
                onDateSelected = {
                    showDatePicker=true
                }
            )

            Spacer(Modifier.height(18.dp))

        if (isLoading) {
            Box (modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center){
                androidx.compose.material3.CircularProgressIndicator(
                    color = Color.LightGray
                )
            }
        }
        else {
            TasksMainView(
                modifier = modifier.fillMaxWidth(),
                allTasksList = taskList,
                viewModel = viewModel
            )
        }
            if (showDatePicker) {
                DatePickerSample(
                    selectedDate = selectedDate,
                    { date ->
                        viewModel.updateSelectedDate(date)
                        showDatePicker = false
                    },
                    onDismiss = { showDatePicker = false }
                )
            }
        }
            FloatingActionButton(
                onClick = {
                    navController.navigate("taskinputscreen")
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 32.dp),
                shape = RoundedCornerShape(45.dp),
                containerColor = MaterialTheme.colorScheme.primary,

            ) {
                Icon(Icons.Default.Add, contentDescription = "Add item")
            }
    }
}

@Composable
fun TopContentView(
    modifier: Modifier = Modifier,
    //viewModel: TaskViewModel,
    currentDate: Date,
    onDateSelected: (Date) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp,26.dp,16.dp,0.dp)) {
        // Row to set date and day
        Row(
            modifier = Modifier.fillMaxWidth(1f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .clickable{
                        onDateSelected(currentDate)
                        //showDatePicker = true
                    }
            ) {
                Row(modifier = Modifier.padding(0.dp)) {
                    Text(
                        text = SimpleDateFormat("dd", Locale.getDefault()).format(currentDate),
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Column(
                        modifier = Modifier.padding(5.dp,8.dp,0.dp,0.dp)
                    ) {
                        Text(
                            text = SimpleDateFormat("MMM", Locale.getDefault()).format(currentDate).uppercase(), // Month
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = SimpleDateFormat("yyyy", Locale.getDefault()).format(currentDate), // Year
                            fontSize = 14.sp
                        )
                    }
                }
            }
            Text(
                text =SimpleDateFormat("EEEE", Locale.getDefault()).format(currentDate).uppercase(),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier =Modifier.padding(14.dp)
            )
        }
    }
}

@Composable
fun TasksMainView(
    modifier: Modifier = Modifier,
    allTasksList: List<Task>,
    viewModel: TaskViewModel
) {
    if (allTasksList.isEmpty()) {
       Box (modifier = Modifier.fillMaxSize(),
           contentAlignment = Alignment.Center) {   // showNoTaskView
           Text("No Tasks,click the button below to add",
               fontSize = 16.sp,
               color = Color.LightGray
           )
       }
    } else {
        AllTaskListView(modifier = modifier, allTasksList = allTasksList, viewModel = viewModel)
    }
}

@Composable
fun AllTaskListView(
    modifier: Modifier = Modifier,
    allTasksList: List<Task>,
    viewModel: TaskViewModel
) {
    val completedTasks = allTasksList.filter { it.iscompleted }
    val pendingTasks = allTasksList.filter { !it.iscompleted }

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        if (pendingTasks.isNotEmpty()) {
            TaskListView(
                modifier = modifier,
                tasksList = pendingTasks,
                title = "Pending Task",
                viewModel = viewModel
            )
            // LazyList for pending tasks
        }

        if (completedTasks.isNotEmpty()) {
            TaskListView(
                modifier = modifier,
                tasksList = completedTasks,
                title = "Completed",
                viewModel = viewModel
            )
            // LazyList for completed tasks
        }
    }
}

@Composable
fun TaskListView(
    modifier: Modifier = Modifier,
    tasksList: List<Task>,
    viewModel: TaskViewModel,
    title: String
) {
    var isExpanded by remember { mutableStateOf(true) }
    Column(modifier = modifier
        .clip(RoundedCornerShape(16.dp))
        .background(Color.LightGray)) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        )
        {
            TaskListHeaderView(
                modifier = Modifier,
                title = title,
                number = tasksList.size,
                isExpanded = isExpanded,
                dropDownAction = {
                    isExpanded=!isExpanded
                }
            )
        }
        if (isExpanded) {
            LazyColumn(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .height((tasksList.size * 50).dp)
            ) {
                items(tasksList) { task ->
                    TaskItemRow(task = task, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun TaskListHeaderView(modifier: Modifier = Modifier,title: String,number: Int,
                       isExpanded:Boolean,dropDownAction: () -> Unit) {
    Row(modifier.fillMaxWidth().clickable(onClick = { dropDownAction()}).padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween)
    {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("($number)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Icon(
            imageVector = if (isExpanded)
                Icons.Default.KeyboardArrowUp
            else
                Icons.Default.KeyboardArrowDown,
            contentDescription = "Expand or collapse",
            tint = Color.Black
        )
    }

}

//composable to access tasks

@Composable
fun TaskItemRow(task: Task,
                viewModel: TaskViewModel
               ) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(10.dp)
            .clickable {
                viewModel.updateTask(task.copy(iscompleted = !task.iscompleted))
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            task.task_name,
            modifier = Modifier.weight(0.5f),
            color = if (task.iscompleted) Color.Gray else Color.Black,
            fontSize = 18.sp
        )

        Box(
            modifier = Modifier
                .size(25.dp)
                .clip(CircleShape)
                .background(if (task.iscompleted) Color.Blue.copy(0.3f) else Color.Transparent)
                .border(
                    width = 2.dp,
                    color = if (task.iscompleted) Color.Transparent else Color.Gray,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (task.iscompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "completed",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}