
package com.example.mobileproject.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mobileproject.data.Priority
import com.example.mobileproject.data.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TaskViewModel : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    fun addTask(
        title: String,
        description: String,
        priority: Priority = Priority.MEDIUM
    ) {
        if (title.isBlank()) return

        _tasks.update { currentTasks ->
            val newId = (currentTasks.maxOfOrNull { it.id } ?: 0L) + 1L

            val newTask = Task(
                id = newId,
                title = title.trim(),
                description = description.trim(),
                priority = priority
            )

            currentTasks + newTask
        }
    }

    fun updateTask(
        id: Long,
        title: String,
        description: String,
        priority: Priority
    ) {
        if (title.isBlank()) return

        _tasks.update { currentTasks ->
            currentTasks.map { task ->
                if (task.id == id) {
                    task.copy(
                        title = title.trim(),
                        description = description.trim(),
                        priority = priority
                    )
                } else {
                    task
                }
            }
        }
    }

    fun deleteTask(id: Long) {
        _tasks.update { currentTasks ->
            currentTasks.filterNot { it.id == id }
        }
    }

    fun toggleTaskCompletion(id: Long) {
        _tasks.update { currentTasks ->
            currentTasks.map { task ->
                if (task.id == id) {
                    task.copy(isCompleted = !task.isCompleted)
                } else {
                    task
                }
            }
        }
    }
}
