package com.example.mobileproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileproject.data.AppDatabase
import com.example.mobileproject.data.Priority
import com.example.mobileproject.data.Task
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * AndroidViewModel يحمل الـ Application context تلقائياً،
 * مما يتيح استخدام Room بدون الحاجة لـ ViewModelFactory مخصص.
 */
class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).taskDao()

    // يحوّل الـ Flow من Room إلى StateFlow مرتبط بدورة حياة الـ ViewModel
    val tasks: StateFlow<List<Task>> = dao.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addTask(
        title: String,
        description: String,
        priority: Priority = Priority.MEDIUM
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            dao.insertTask(
                Task(
                    title = title.trim(),
                    description = description.trim(),
                    priority = priority
                )
            )
        }
    }

    fun updateTask(
        id: Long,
        title: String,
        description: String,
        priority: Priority
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            dao.updateTask(
                Task(
                    id = id,
                    title = title.trim(),
                    description = description.trim(),
                    priority = priority
                )
            )
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            dao.getTaskById(id)?.let { dao.deleteTask(it) }
        }
    }

    fun toggleTaskCompletion(id: Long) {
        viewModelScope.launch {
            dao.getTaskById(id)?.let { task ->
                dao.updateTask(task.copy(isCompleted = !task.isCompleted))
            }
        }
    }

    /** suspend — تُستدعى داخل LaunchedEffect أو coroutine scope */
    suspend fun getTaskById(id: Long): Task? = dao.getTaskById(id)
}
