package com.owais.milktracker.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.owais.milktracker.data.model.MilkEntry
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EntryDialog(
    date: LocalDate,
    initialEntry: MilkEntry?,
    onDismiss: () -> Unit,
    onSave: (MilkEntry) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var quantity by remember { mutableStateOf(initialEntry?.quantity?.toString() ?: "") }
    var isBorrowed by remember { mutableStateOf(initialEntry?.isBorrowed ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val qty = quantity.toDoubleOrNull()
                if (qty != null) {
                    onSave(
                        MilkEntry(
                            date = date,
                            quantity = qty,
                            isBorrowed = isBorrowed
                        )
                    )

                }
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                if (initialEntry != null) {
                    TextButton(
                        onClick = {
                            onSave(
                                MilkEntry(
                                    date = date,
                                    quantity = 0.0,
                                    isBorrowed = isBorrowed // or default to false
                                )
                            )
                            onDelete?.invoke()
                            onDismiss()
                        }

                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        },

        title = { Text("Milk Entry for ${date.dayOfMonth}/${date.monthValue}") },
        text = {
            Column {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity (Litres)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Text("Type: ")
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = !isBorrowed,
                            onClick = { isBorrowed = false }
                        )
                        Text("Sold")
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(
                            selected = isBorrowed,
                            onClick = { isBorrowed = true }
                        )
                        Text("Borrowed")
                    }
                }
            }
        }
    )
}
