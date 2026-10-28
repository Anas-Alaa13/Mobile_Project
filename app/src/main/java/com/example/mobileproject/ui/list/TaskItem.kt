package com.example.mobileproject.ui.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobileproject.data.Priority
import com.example.mobileproject.data.Task

/**
 * Shows ONE task as a card: checkbox + title/description + delete button.
 *
 * This composable is "stateless": it owns no data. Everything it needs comes in
 * through parameters, and every user action goes out through a callback.
 */
@Composable
fun TaskItem(
    task: Task,
    onToggleCompleted: () -> Unit,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            // Completed tasks get a "dimmed" background so they stand out from active ones.
            containerColor = if (task.isCompleted) colors.surfaceVariant else colors.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (task.isCompleted) 0.dp else 1.dp
        ),
        border = BorderStroke(1.dp, colors.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // `checked` always comes from the Task. We ignore the new value on purpose:
            // the ViewModel flips the state, then the list re-draws with the new Task.
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggleCompleted() },
                modifier = Modifier.semantics { contentDescription = task.title }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (task.isCompleted) colors.onSurfaceVariant else colors.onSurface,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = when (task.priority) {
                        Priority.HIGH -> "High priority"
                        Priority.MEDIUM -> "Medium priority"
                        Priority.LOW -> "Low priority"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = when (task.priority) {
                        Priority.HIGH -> colors.error
                        Priority.MEDIUM -> colors.tertiary
                        Priority.LOW -> colors.onSurfaceVariant
                    }
                )
            }

            // IconButton is 48dp x 48dp by default = a comfortable touch target.
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete task ${task.title}",
                    tint = colors.error
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Previews (only visible in Android Studio's Split/Design view, not in the app)
// ---------------------------------------------------------------------------

@Preview(name = "Active task", showBackground = true, widthDp = 360)
@Composable
private fun TaskItemActivePreview() {
    MaterialTheme {
        TaskItem(
            task = Task(
                id = 1L,
                title = "Buy groceries",
                description = "Milk, eggs and bread",
                priority = Priority.HIGH
            ),
            onToggleCompleted = {},
            onDeleteClick = {},
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Completed task", showBackground = true, widthDp = 360)
@Composable
private fun TaskItemCompletedPreview() {
    MaterialTheme {
        TaskItem(
            task = Task(
                id = 2L,
                title = "Submit assignment",
                description = "Upload the PDF to the university portal",
                isCompleted = true,
                priority = Priority.LOW
            ),
            onToggleCompleted = {},
            onDeleteClick = {},
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Long text, narrow screen", showBackground = true, widthDp = 320)
@Composable
private fun TaskItemLongTextPreview() {
    MaterialTheme {
        TaskItem(
            task = Task(
                id = 3L,
                title = "Prepare the complete presentation for the final mobile development project discussion",
                description = "Include the architecture diagram, the database design, screenshots of every " +
                    "screen, and a short demo of adding, completing and deleting tasks.",
                priority = Priority.MEDIUM
            ),
            onToggleCompleted = {},
            onDeleteClick = {},
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
