package com.example.task_management_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CalendarDayView(day: Int, isHighlighted: Boolean, modifier: Modifier = Modifier) {
    Box(
            modifier = modifier
                    .background(if (isHighlighted) Color.Yellow else Color.Transparent)
            .padding(16.dp)
    ) {
        Text(text = "$day")
    }
}

@Preview
@Composable
fun CalendarDayViewPreview() {
    CalendarDayView(day = 15, isHighlighted = true)
}
