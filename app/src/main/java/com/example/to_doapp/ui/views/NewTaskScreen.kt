package com.example.to_doapp.ui.views

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.to_doapp.viewModels.TaskViewModel
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun TaskInputScreen(modifier: Modifier = Modifier, navController: NavHostController, viewModel: TaskViewModel) {
    var taskName by remember { mutableStateOf("") }
    val selectedDate by viewModel.selectedDate.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showError by remember {mutableStateOf(false) }
    var showSuccess by remember{mutableStateOf(false)}
    var showPopup by remember { mutableStateOf(false) }


    BackHandler {
        if (taskName.isEmpty()){
            navController.popBackStack()
        }
        else{
            showPopup = true}
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column (modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconButton  (
                onClick = {
                    if (taskName.isEmpty()){
                        navController.popBackStack()
                    }
                    else{
                        showPopup = true}
                },
                modifier = Modifier
                    .align(Alignment.Start) // Places icon at top-left
                    .padding(8.dp,26.dp,0.dp,0.dp)            // Adds some spacing from the start and top
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                   // modifier = Modifier.size(24.dp),
                    contentDescription = "Back"
                )
            }

            TextField(
                value = taskName,
                onValueChange = {
                    taskName = it
                    // as soon as starts entering error message or success message will disappear
                    //if (showSuccess)showSuccess=false
                    if (showError) showError=false
                },
                modifier = Modifier.padding(20.dp)
                    .height(200.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                label = { Text("Enter Task", fontSize = 16.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.LightGray.copy(0.4f),
                    unfocusedContainerColor = Color.LightGray.copy(0.4f),
                    focusedIndicatorColor = Color.Transparent, // when clicking textfield  no underline
                    unfocusedIndicatorColor = Color.Transparent, //when user not clicking no underline
                )
            )


            Spacer(modifier = Modifier.height(13.dp))
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 20.dp)
                    .clickable { showDatePicker = true }
            ) {
                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Date")

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(selectedDate),
                    fontSize = 18.sp
                )

            }
            if (showError){               //it is to give message when add task button clicked without entering the task
                Text("please enter the task to add",
                    color = Color.Red,
                    fontSize = 16.sp,
                    )
            }

            if (showSuccess){             // it is to give message when add task button clicked with entering the task
                Text("you have successfully entered your task")
            }

            if(showSuccess){
                LaunchedEffect(showSuccess) {
                    kotlinx.coroutines.delay(1000L) //show success message for 1 second
                    navController.popBackStack()       //after success giong back to home screen
                    //showSuccess=false
                }
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

        },
        modifier=Modifier
            .padding(16.dp)
            .align(Alignment.BottomEnd)) {
            Text("Add to List")
        }



    }
    if (showDatePicker) {
        DatePickerSample(
            selectedDate = selectedDate,           // for datepicker to hold selected date
            { date ->
                viewModel.updateSelectedDate(date)     // updating the state of _selectedDate (it holds the date state)
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