package com.example.mobileproject.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM // إضافة حقل الأولوية بقيمة افتراضية
)

// تعريف مستويات الأولوية
enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}