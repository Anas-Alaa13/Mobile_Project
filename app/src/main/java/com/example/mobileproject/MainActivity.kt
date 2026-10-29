package com.example.mobileproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.mobileproject.ui.navigation.AppNavigation
import com.example.mobileproject.ui.theme.TodoAppTheme
import com.example.mobileproject.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {

    // AndroidViewModel يُنشأ تلقائياً بدون Factory لأن النظام يمرر الـ Application
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
