package com.example.mobileproject.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mobileproject.data.Priority
import com.example.mobileproject.data.Task
import com.example.mobileproject.viewmodel.TaskViewModel

// Cards never get narrower than this; wider windows get more columns.
private val MinCardWidth = 320.dp

// On very wide screens the content stays centered instead of stretching edge to edge.
private val MaxContentWidth = 1000.dp

/** The three filters shown as chips. The filtering rule lives in ONE place: [matches]. */
private enum class TaskFilter(val label: String) {
    ALL("All"),
    ACTIVE("Active"),
    COMPLETED("Completed");

    fun matches(task: Task): Boolean = when (this) {
        ALL -> true
        ACTIVE -> !task.isCompleted
        COMPLETED -> task.isCompleted
    }
}

/**
 * Entry point used by AppNavigation.
 * It only connects the ViewModel to the UI; all drawing happens in [TaskListContent].
 */
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    onAddClick: () -> Unit,
    onTaskClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // Lifecycle-aware: collection stops while the app is in the background.
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    TaskListContent(
        tasks = tasks,
        onAddClick = onAddClick,
        onTaskClick = onTaskClick,
        onToggleCompleted = viewModel::toggleTaskCompletion,
        onDeleteTask = viewModel::deleteTask,
        modifier = modifier
    )
}

/**
 * Stateless screen content: receives a plain list + callbacks.
 * Easy to preview and test because it never touches the ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TaskListContent(
    tasks: List<Task>,
    onAddClick: () -> Unit,
    onTaskClick: (Long) -> Unit,
    onToggleCompleted: (Long) -> Unit,
    onDeleteTask: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // UI-only state (survives rotation thanks to rememberSaveable).
    var selectedFilter by rememberSaveable { mutableStateOf(TaskFilter.ALL) }
    // We remember only the ID of the task to delete, never the Task object,
    // so the dialog can never show an outdated copy.
    var pendingDeleteId by rememberSaveable { mutableStateOf<Long?>(null) }

    val visibleTasks = remember(tasks, selectedFilter) {
        tasks.filter { selectedFilter.matches(it) }
    }
    val counts = remember(tasks) {
        TaskFilter.entries.associateWith { filter -> tasks.count { filter.matches(it) } }
    }
    val pendingDeleteTask = pendingDeleteId?.let { id -> tasks.firstOrNull { it.id == id } }

    Scaffold(
        modifier = modifier,
        topBar = { CenterAlignedTopAppBar(title = { Text("My Tasks") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add task") }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = MaxContentWidth)
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                FilterRow(
                    selected = selectedFilter,
                    counts = counts,
                    onSelected = { selectedFilter = it }
                )

                when {
                    tasks.isEmpty() -> EmptyState(
                        title = "No tasks yet",
                        message = "Tap the button below to add your first task.",
                        actionLabel = "Add your first task",
                        onActionClick = onAddClick,
                        modifier = Modifier.weight(1f)
                    )

                    visibleTasks.isEmpty() -> EmptyState(
                        title = if (selectedFilter == TaskFilter.ACTIVE) "No active tasks" else "No completed tasks",
                        message = if (selectedFilter == TaskFilter.ACTIVE) {
                            "Everything is done. Nice work!"
                        } else {
                            "Finish a task and it will show up here."
                        },
                        modifier = Modifier.weight(1f)
                    )

                    else -> LazyVerticalGrid(
                        // 1 column on phones, 2+ columns on landscape/tablets.
                        columns = GridCells.Adaptive(minSize = MinCardWidth),
                        modifier = Modifier.weight(1f),
                        // Big bottom padding so the FAB never hides the last card.
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = visibleTasks, key = { it.id }) { task ->
                            TaskItem(
                                task = task,
                                onToggleCompleted = { onToggleCompleted(task.id) },
                                onDeleteClick = { pendingDeleteId = task.id },
                                onClick = { onTaskClick(task.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

    // The dialog exists only while there is a task waiting for confirmation.
    pendingDeleteTask?.let { task ->
        DeleteTaskDialog(
            taskTitle = task.title,
            onConfirm = {
                onDeleteTask(task.id)
                pendingDeleteId = null
            },
            onDismiss = { pendingDeleteId = null }
        )
    }
}

@Composable
private fun FilterRow(
    selected: TaskFilter,
    counts: Map<TaskFilter, Int>,
    onSelected: (TaskFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    // horizontalScroll: on very narrow screens the chips can be swiped instead of being cut off.
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TaskFilter.entries.forEach { filter ->
            FilterChip(
                selected = filter == selected,
                onClick = { onSelected(filter) },
                label = { Text("${filter.label} (${counts[filter] ?: 0})") }
            )
        }
    }
}

@Composable
private fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    // verticalScroll keeps the content reachable in short (landscape) windows.
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onActionClick != null) {
            Spacer(Modifier.height(24.dp))
            Button(onClick = onActionClick) { Text(actionLabel) }
        }
    }
}

@Composable
private fun DeleteTaskDialog(
    taskTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete task?") },
        text = {
            Text(
                text = "\"$taskTitle\" will be permanently deleted.",
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

private val previewTasks = listOf(
    Task(1L, "Buy groceries", "Milk, eggs and bread", false, Priority.HIGH),
    Task(2L, "Submit assignment", "Upload the PDF to the portal", true, Priority.LOW),
    Task(3L, "Read chapter 5", "", false, Priority.MEDIUM),
    Task(
        4L,
        "Prepare the complete presentation for the final mobile development project discussion",
        "Include the architecture diagram, database design, screenshots of every screen and a short demo.",
        false,
        Priority.MEDIUM
    ),
    Task(5L, "Call the doctor", "Book an appointment for next week", true, Priority.HIGH)
)

@Preview(name = "Phone - populated", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun TaskListPhonePreview() {
    MaterialTheme {
        TaskListContent(previewTasks, {}, {}, {}, {})
    }
}

@Preview(name = "Phone - empty", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun TaskListEmptyPreview() {
    MaterialTheme {
        TaskListContent(emptyList(), {}, {}, {}, {})
    }
}

@Preview(name = "Small phone", showBackground = true, widthDp = 320, heightDp = 568)
@Composable
private fun TaskListSmallPhonePreview() {
    MaterialTheme {
        TaskListContent(previewTasks, {}, {}, {}, {})
    }
}

@Preview(name = "Landscape", showBackground = true, widthDp = 800, heightDp = 360)
@Composable
private fun TaskListLandscapePreview() {
    MaterialTheme {
        TaskListContent(previewTasks, {}, {}, {}, {})
    }
}

@Preview(name = "Tablet", showBackground = true, widthDp = 1100, heightDp = 700)
@Composable
private fun TaskListTabletPreview() {
    MaterialTheme {
        TaskListContent(previewTasks, {}, {}, {}, {})
    }
}
