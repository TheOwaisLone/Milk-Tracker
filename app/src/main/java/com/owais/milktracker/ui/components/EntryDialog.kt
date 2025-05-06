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
import androidx.compose.material3.Card
import androidx.compose.ui.text.font.FontWeight
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.owais.milktracker.data.model.MilkEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale

// Define the dataStore extension for preferences storage
private val Context.dataStore by preferencesDataStore(name = "milk_preferences")

// Define the keys used for storing and retrieving preferences
private val LAST_QUANTITY = stringPreferencesKey("last_quantity")
private val LAST_IS_BORROWED = booleanPreferencesKey("last_is_borrowed")

// Composable function to show the entry dialog
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

    // Format date like "Apr 4"
    val formattedDate = remember(date) {
        "${date.month.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault())} ${date.dayOfMonth}"
    }

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
        title = {
            Text(
                text = "Milk Entry — $formattedDate",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity (Litres)") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Type", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { isBorrowed = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isBorrowed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Sold")
                        }

                        Button(
                            onClick = { isBorrowed = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBorrowed) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Borrowed")
                        }
                    }
                }
            }
        },
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
                    TextButton(onClick = {
                        onDelete?.invoke()
                        scope.launch {
                            context.dataStore.edit { prefs ->
                                prefs.remove(LAST_QUANTITY)
                                prefs.remove(LAST_IS_BORROWED)
                            }
                        }
                        onDismiss()
                    }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
