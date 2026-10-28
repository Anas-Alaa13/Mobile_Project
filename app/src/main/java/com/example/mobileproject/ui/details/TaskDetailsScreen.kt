package com.example.mobileproject.ui.details

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mobileproject.data.Priority
import com.example.mobileproject.data.Task
import com.example.mobileproject.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsScreen(
    taskId: Long,
    viewModel: TaskViewModel,
    onEdit: (Long) -> Unit,
    onBack: () -> Unit
) {
    var task by remember { mutableStateOf<Task?>(null) }
    val context = LocalContext.current

    LaunchedEffect(taskId) {
        task = viewModel.getTaskById(taskId)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Task Details") }) }
    ) { padding ->
        task?.let { t ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(t.title, style = MaterialTheme.typography.headlineSmall)
                Text(t.description.ifBlank { "No description" })

                val priorityLabel = when (t.priority) {
                    Priority.LOW -> "Low"
                    Priority.MEDIUM -> "Medium"
                    Priority.HIGH -> "High"
                }
                Text("Priority: $priorityLabel")
                Text("Status: ${if (t.isCompleted) "Completed" else "Pending"}")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { onEdit(t.id) }) { Text("Edit") }

                    // Implicit Intent للمشاركة
                    Button(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, t.title)
                            putExtra(Intent.EXTRA_TEXT, "Task: ${t.title}\n${t.description}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share task via"))
                    }) { Text("Share") }

                    OutlinedButton(onClick = onBack) { Text("Back") }
                }
            }
        }
    }
}