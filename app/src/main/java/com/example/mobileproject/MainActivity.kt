package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.todoapp.ui.navigation.AppNavigation
import com.example.todoapp.ui.theme.TodoAppTheme
import com.example.todoapp.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoAppTheme {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}