package com.example.mobileproject.ui.navigation

object Routes {
    const val LIST = "list"
    const val ADD_EDIT = "add_edit/{taskId}"
    const val DETAILS = "details/{taskId}"

    fun addEdit(taskId: Long = -1L) = "add_edit/$taskId"
    fun details(taskId: Long) = "details/$taskId"
}
