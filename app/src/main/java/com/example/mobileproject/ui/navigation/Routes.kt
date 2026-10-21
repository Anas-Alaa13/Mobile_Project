package com.example.todoapp.ui.navigation

object Routes {
    const val LIST = "list"
    const val ADD_EDIT = "add_edit/{taskId}"
    const val DETAILS = "details/{taskId}"

    fun addEdit(taskId: Int = -1) = "add_edit/$taskId"
    fun details(taskId: Int) = "details/$taskId"
}