package com.example.to_doapp
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import  androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.util.TableInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun TaskHomeScreen(
    modifier: Modifier = Modifier,navController: NavHostController ,viewModel: TaskViewModel
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val taskList by viewModel.tasks.collectAsState()  //observe tasks from viewmodel
    var showDatePicker by remember { mutableStateOf(false) }
    LaunchedEffect(selectedDate) {  // Load tasks once when screen opens
        viewModel.tasksforselecteddate()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp,26.dp,16.dp,0.dp)) {
            // Row to set date and day
            Row(
                modifier = Modifier.fillMaxWidth(1f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // This box is to hold the date
                Box(
                    modifier = Modifier
                        .clickable{showDatePicker=true}
                ) {
                    Row(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = SimpleDateFormat("dd", Locale.getDefault())
                                .format(selectedDate),
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Column(
                            modifier = Modifier.padding(5.dp,8.dp,0.dp,0.dp)
                        ) {
                            Text(
                                text = SimpleDateFormat("MMM", Locale.getDefault()).format(
                                    selectedDate
                                ),  // Month
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = SimpleDateFormat("yyyy", Locale.getDefault()).format(
                                    selectedDate
                                ), // Year
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                Text(
                    text = SimpleDateFormat("EEEE", Locale.getDefault())
                        .format(selectedDate),
                    fontSize = 20.sp,
                    modifier =Modifier.padding(14.dp)
                )


            }

        Spacer(Modifier.height(8.dp))

        Text(
            " Tasks for the day ",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 22.sp,
            color = Color.Blue
        )

        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(taskList) { task ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .clickable {
                            viewModel.updateTask(task.copy(iscompleted = !task.iscompleted))
                        },
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    //Text( text = task.task_name, modifier = Modifier.padding(4.dp))

                    Text(task.task_name, modifier = Modifier.weight(0.5f), fontSize = 18.sp)

                    Box(
                        modifier = Modifier
                            .size(25.dp)
                            .clip(CircleShape)
                            //  If completed, Green background. If not, Transparent.
                            .background(if (task.iscompleted) Color.Green else Color.Transparent)
                            //  Add a border. If not completed, show Gray border. If completed, transparent border.
                            .border(
                                width = 2.dp,
                                color = if (task.iscompleted) Color.Transparent else Color.Gray,
                                shape = CircleShape
                            )
                            ,
                        contentAlignment = Alignment.Center
                    ){
                        if (task.iscompleted){
                            //  Use the simple 'Check' icon (tick mark) inside the green circle
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "completed",
                                tint= Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
        if (showDatePicker) {
            DatePickerSample(
                selectedDate = selectedDate,
                { date ->
                viewModel.updateSelectedDate(date)
                showDatePicker = false
            },
                onDismiss = {showDatePicker=false}
            )
        }
    }
        FloatingActionButton(
            onClick = {
                navController.navigate("taskinput")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 32.dp),
            shape = RoundedCornerShape(50.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add item")
        }
    }
}


@Composable
fun TaskInputScreen(modifier: Modifier = Modifier, navController: NavHostController, viewModel: TaskViewModel) {
    var taskName by remember { mutableStateOf("") }
    val selectedDate by viewModel.selectedDate.collectAsState()
    var isDateSelected by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showError by remember {mutableStateOf(false) }
    var showSuccess by remember{mutableStateOf(false)}
    var showPopup by remember { mutableStateOf(false) }

    BackHandler {
        showPopup = true
    }
    Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center) {
        Column (modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {

            if (showError){               //it is to give message when add task button clicked without entering the task
                Text("please enter task to insert")
            }

            if (showSuccess){             // it is to give message when add task button clicked with entering the task
                Text("you have successfully entered your task")
            }
            if(showSuccess){
                LaunchedEffect(showSuccess) {
                    kotlinx.coroutines.delay(3000L) //show success message for 3 seconds
                    navController.popBackStack()       //after success giong back to home screen
                    //showSuccess=false
                }
                           }
            TextField(
                value = taskName, onValueChange = {
                    taskName = it
                    // as soon as starts entering error message or success message will disappear
                    //if (showSuccess)showSuccess=false 
                    if (showError)showError=false
                },
                modifier = Modifier.padding(20.dp),
                shape = RoundedCornerShape(16.dp),
                label = { Text("Enter Task", fontSize = 16.sp) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent, // when clicking textfield  no underline
                    unfocusedIndicatorColor = Color.Transparent, //when user not clicking no underline
                )
            )


            Spacer(modifier = Modifier.height(13.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { showDatePicker = true }) {
                    Text("select date")
                }
                Spacer(modifier = Modifier.width(10.dp))
                if (isDateSelected) {                                // after isDateselected comes true date picker (clicking ok) ,now telling what to do
                    Text(
                        text = " ${
                            SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(
                                selectedDate
                            )
                        }", fontSize = 18.sp
                    )
                }
            }

            Button(onClick = {
              val validTaskName=taskName.trim() //removing spaces from start and end
                if (validTaskName.isNotEmpty()) {
                    viewModel.insertTask(validTaskName)
                    taskName = ""
                    showError=false
                    showSuccess=true
                }
                else {showError=true
                showSuccess=false}

            }) {
                Text("Add Task")
            }
        }
        FilledIconButton (
            onClick = { showPopup = true },
            modifier = Modifier
                .align(Alignment.TopStart) // Places icon at top-left
                .padding(16.dp,26.dp,0.dp,0.dp)            // Adds some spacing from the start and top
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
           }
    if (showDatePicker) {
        DatePickerSample(
            selectedDate = selectedDate,           // for datepicker to hold selected date
            { date ->
                viewModel.updateSelectedDate(date)     // updating the state of _selectedDate (it holds the date state)
                isDateSelected=true                //on clicking the date and confirming by ok then isDateSelected comes true
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
    if (showPopup) {                            //this tells when showPopup is true then perform the action inside me
        AlertDialog(
            onDismissRequest = { showPopup = false },      //first Popup comes and displays the message when no clicked popup goes away and will stay in same screen

            title = { Text("Are you sure?") },
            text = { Text("Do you want to go back ?") },

            confirmButton = {                   // popup comes when clicked yes then popup goes away and directing to previous screen
                TextButton(onClick = {
                    showPopup = false
                    navController.popBackStack()   // go back
                }) {
                    Text("Yes")
                }
            },

            dismissButton = {
                TextButton(onClick = { showPopup = false }) {
                    Text("No")
                }
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerSample(
    selectedDate: Date,
    onDateSelected: (Date) -> Unit,
    onDismiss:() -> Unit
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.time
    )
    DatePickerDialog (
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton (
                onClick = {
                    state.selectedDateMillis?.let { onDateSelected(Date(it)) }
                }
            ) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel") }
        }
    ) {
        DatePicker(state = state)
    }
}


@Composable
fun appNavController(modifier: Modifier = Modifier, viewModel: TaskViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "taskhome") {
        composable("taskhome") {
            TaskHomeScreen(modifier = Modifier.padding(6.dp), navController, viewModel)
        }
        composable("taskinput")
        {
            TaskInputScreen(modifier = Modifier.padding(5.dp), navController, viewModel)
        }
    }

}