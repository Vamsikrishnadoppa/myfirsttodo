package com.example.to_doapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.to_doapp.ui.views.TaskHomeScreen
import com.example.to_doapp.ui.views.TaskInputScreen
import com.example.to_doapp.viewModels.TaskViewModel
import com.example.to_doapp.viewModels.TaskViewModelFactory

class MainActivity : ComponentActivity() {
    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(application)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            appNavController(viewModel = taskViewModel)
        }
    }
}

@Composable
fun appNavController(modifier: Modifier = Modifier, viewModel: TaskViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "taskhome") {
        composable("taskhome") {
            TaskHomeScreen(modifier = Modifier.padding(6.dp), navController, viewModel)
        }
        composable("taskinputscreen")
        {
            TaskInputScreen(modifier = Modifier.padding(5.dp), navController, viewModel)
        }
    }

}