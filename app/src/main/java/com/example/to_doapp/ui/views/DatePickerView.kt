package com.example.to_doapp.ui.views
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerSample(
    selectedDate: Date,
    onDateSelected: (Date) -> Unit,
    onDismiss:() -> Unit
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.time
    )
    DatePickerDialog (
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton (
                onClick = {
                    state.selectedDateMillis?.let { onDateSelected(Date(it)) }
                }
            ) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel") }
        }
    ) {
        DatePicker(state = state)
    }
}
