package com.example.mobileproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mobileproject.ui.addedit.AddEditTaskScreen
import com.example.mobileproject.ui.details.TaskDetailsScreen
import com.example.mobileproject.ui.list.TaskListScreen
import com.example.mobileproject.viewmodel.TaskViewModel

@Composable
fun AppNavigation(viewModel: TaskViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LIST) {

        composable(Routes.LIST) {
            TaskListScreen(
                viewModel = viewModel,
                onAddClick = { navController.navigate(Routes.addEdit()) },
                onTaskClick = { id -> navController.navigate(Routes.details(id)) }
            )
        }

        composable(
            route = Routes.ADD_EDIT,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: -1L
            AddEditTaskScreen(
                taskId = taskId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.DETAILS,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: return@composable
            TaskDetailsScreen(
                taskId = taskId,
                viewModel = viewModel,
                onEdit = { id -> navController.navigate(Routes.addEdit(id)) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}