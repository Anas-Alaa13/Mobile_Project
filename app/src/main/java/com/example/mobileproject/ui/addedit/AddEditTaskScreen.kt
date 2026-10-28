package com.example.mobileproject.ui.addedit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobileproject.data.Priority
import com.example.mobileproject.data.Task
import com.example.mobileproject.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    taskId: Long,                       // -1L = إضافة جديدة
    viewModel: TaskViewModel,
    onBack: () -> Unit
) {
    var existingTask by remember { mutableStateOf<Task?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var titleError by remember { mutableStateOf(false) }

    // تحميل المهمة في حالة التعديل
    LaunchedEffect(taskId) {
        if (taskId != -1L) {
            viewModel.getTaskById(taskId)?.let { task ->
                existingTask = task
                title = task.title
                description = task.description
                priority = task.priority
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (taskId == -1L) "Add Task" else "Edit Task") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it; titleError = false },
                label = { Text("Title") },
                isError = titleError,
                supportingText = { if (titleError) Text("Title cannot be empty") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Priority", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(Priority.LOW to "Low", Priority.MEDIUM to "Medium", Priority.HIGH to "High")
                    .forEach { (p, label) ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(label) }
                        )
                    }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onBack) { Text("Cancel") }
                Button(onClick = {
                    if (title.isBlank()) {
                        titleError = true
                    } else {
                        if (existingTask != null) {
                            viewModel.updateTask(
                                id = existingTask!!.id,
                                title = title.trim(),
                                description = description.trim(),
                                priority = priority
                            )
                        } else {
                            viewModel.addTask(
                                title = title.trim(),
                                description = description.trim(),
                                priority = priority
                            )
                        }
                        onBack()
                    }
                }) { Text("Save") }
            }
        }
    }
}