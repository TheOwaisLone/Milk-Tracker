package com.owais.milktracker.ui.components

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.owais.milktracker.data.model.MilkEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

private val Context.dataStore by preferencesDataStore(name = "milk_preferences")

private val LAST_QUANTITY = stringPreferencesKey("last_quantity")
private val LAST_IS_BORROWED = booleanPreferencesKey("last_is_borrowed")

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EntryDialog(
    date: LocalDate,
    initialEntry: MilkEntry?,
    onDismiss: () -> Unit,
    onSave: (MilkEntry) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var quantity by remember { mutableStateOf("") }
    var isBorrowed by remember { mutableStateOf(false) }

    // Use correct logic: preload values only for new entries
    LaunchedEffect(initialEntry) {
        if (initialEntry == null) {
            val prefs = context.dataStore.data.first()
            quantity = prefs[LAST_QUANTITY] ?: ""
            isBorrowed = prefs[LAST_IS_BORROWED] ?: false
        } else {
            quantity = initialEntry.quantity.toString()
            isBorrowed = initialEntry.isBorrowed
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val qty = quantity.toDoubleOrNull()
                if (qty != null) {
                    val newEntry = MilkEntry(
                        id = initialEntry?.id ?: 0,
                        date = date,
                        quantity = qty,
                        isBorrowed = isBorrowed
                    )
                    onSave(newEntry)

                    // ✅ Save these values for future new entries only
                    scope.launch {
                        context.dataStore.edit { prefs ->
                            prefs[LAST_QUANTITY] = quantity
                            prefs[LAST_IS_BORROWED] = isBorrowed
                        }
                    }
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
                            onDelete?.invoke()
                            // ❗ Clear saved defaults only if this was the last entry (optional)
                            scope.launch {
                                context.dataStore.edit { prefs ->
                                    prefs.remove(LAST_QUANTITY)
                                    prefs.remove(LAST_IS_BORROWED)
                                }
                            }
                            onDismiss()
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        },
        title = {
            Text("Milk Entry for ${date.dayOfMonth}/${date.monthValue}")
        },
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
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
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
