package com.example.todoapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todoapp.ui.addedit.AddEditTaskScreen
import com.example.todoapp.ui.details.TaskDetailsScreen
import com.example.todoapp.ui.list.TaskListScreen
import com.example.todoapp.viewmodel.TaskViewModel

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
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: -1
            AddEditTaskScreen(
                taskId = taskId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.DETAILS,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: return@composable
            TaskDetailsScreen(
                taskId = taskId,
                viewModel = viewModel,
                onEdit = { id -> navController.navigate(Routes.addEdit(id)) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}