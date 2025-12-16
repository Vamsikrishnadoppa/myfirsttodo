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
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.room.util.TableInfo
import com.example.to_doapp.R
import com.example.to_doapp.db.model.Task
import com.example.to_doapp.viewModels.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.List

@Composable
fun TaskHomeScreen(
    modifier: Modifier = Modifier, navController: NavHostController, viewModel: TaskViewModel
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val taskList by viewModel.tasks.collectAsState()
    var isLoading by remember { mutableStateOf(true) }

    var showDatePicker by remember { mutableStateOf(false) }
    LaunchedEffect(selectedDate) {
        isLoading = true
        viewModel.tasksForSelectedDate()
        kotlinx.coroutines.delay(500L)
        isLoading = false
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
    ) {

        Column {
            TopContentView(
                modifier = modifier.padding(top = 40.dp),
                viewModel = viewModel,
                currentDate = selectedDate,
                onDateSelected = {
                    showDatePicker = true
                }
            )

            Spacer(Modifier.height(18.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = Color.LightGray
                    )
                }
            } else {
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
            shape = RoundedCornerShape(15.dp),
            containerColor = MaterialTheme.colorScheme.primary,

            ) {
            Icon(Icons.Default.Add, contentDescription = "Add item")
        }
    }
}

@Composable
fun TopContentView(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel,
    currentDate: Date,
    onDateSelected: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp, 50.dp, 24.dp, 0.dp)
            .clickable(
                enabled = true,
                onClick = {
                    onDateSelected()
                }
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(1f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
            ) {
                Row(modifier = Modifier.padding(0.dp)) {
                    Text(
                        text = viewModel.getDateValue(),
                        fontFamily = FontFamily(Font(R.font.opensans_bold)),
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Column(
                        modifier = Modifier.padding(5.dp, 8.dp, 0.dp, 0.dp)
                    ) {
                        Text(
                            text = viewModel.getMonthValue().uppercase(),
                            fontFamily = FontFamily(Font(R.font.opensans_medium)),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = viewModel.getYearValue(),
                            fontFamily = FontFamily(Font(R.font.opensans_light)),
                            fontSize = 14.sp
                        )
                    }
                }
            }
            Text(
                text = SimpleDateFormat("EEEE", Locale.getDefault()).format(currentDate)
                    .uppercase(),
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.opensans_medium)),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(14.dp)
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
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No Tasks,click the button below to add",
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

    Column(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (pendingTasks.isNotEmpty()) {
            TaskListView(
                modifier = modifier,
                tasksList = pendingTasks,
                title = "Pending Task",
                viewModel = viewModel,
                isCollapsable = false
            )
        }
        if (completedTasks.isNotEmpty()) {
            TaskListView(
                modifier = modifier,
                tasksList = completedTasks,
                title = "Completed",
                viewModel = viewModel,
                isCollapsable = true
            )
        }
    }
}

@Composable
fun TaskListView(
    modifier: Modifier = Modifier,
    tasksList: List<Task>,
    viewModel: TaskViewModel,
    title: String,
    isCollapsable: Boolean
) {
    var isExpanded by remember { mutableStateOf(true) }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF1F1F1))
    ) {
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
                isCollapsable = isCollapsable,
                dropDownAction = {
                    if (isCollapsable) {
                        isExpanded = !isExpanded
                    }
                }
            )
        }
        if (isExpanded) {
            LazyColumn(
                modifier = Modifier
                    .padding(top = 16.dp, start = 5.dp)
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
fun TaskListHeaderView(
    modifier: Modifier = Modifier, title: String, number: Int,
    isExpanded: Boolean,
    isCollapsable: Boolean,
    dropDownAction: () -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .clickable(
                isCollapsable,
                onClick = {
                    if (isCollapsable) {
                        dropDownAction()
                    }
                }
            )
            .padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween)
    {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontFamily = FontFamily(Font(R.font.opensans_bold)),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text("($number)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        if (isCollapsable) {
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
}
@Composable
fun TaskItemRow(
    task: Task,
    viewModel: TaskViewModel
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedTaskName by remember { mutableStateOf(task.task_name) }

    if (isEditing) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { isEditing = false },
            title = { Text("Edit Task") },
            text = {
                androidx.compose.material3.TextField(
                    value = editedTaskName,
                    onValueChange = { editedTaskName = it },
                    singleLine = true,
                    colors = androidx.compose.material3.TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        if (editedTaskName.isNotEmpty()) {
                            viewModel.updateTask(task.copy(task_name = editedTaskName))
                            isEditing = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { isEditing = false }) {
                    Text("Cancel")
                }
            }
        )
    }

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
            fontFamily = FontFamily(Font(R.font.opensans_medium)),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = if (task.iscompleted) Color.Gray else Color.Black,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!task.iscompleted) {
                androidx.compose.material3.IconButton(
                    onClick = {
                        editedTaskName = task.task_name
                        isEditing = true
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.Gray
                    )
                }
            }


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
}