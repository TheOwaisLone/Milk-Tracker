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
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.owais.milktracker.data.model.MilkEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

// Define the dataStore extension for preferences storage
private val Context.dataStore by preferencesDataStore(name = "milk_preferences")

// Define the keys used for storing and retrieving preferences
private val LAST_QUANTITY = stringPreferencesKey("last_quantity")
private val LAST_IS_BORROWED = booleanPreferencesKey("last_is_borrowed")

// Composable function to show the entry dialog
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EntryDialog(
    date: LocalDate, // The date of the milk entry
    initialEntry: MilkEntry?, // Existing entry data, null if it's a new entry
    onDismiss: () -> Unit, // Action when dialog is dismissed
    onSave: (MilkEntry) -> Unit, // Action to save the entry
    onDelete: (() -> Unit)? = null // Action to delete the entry, if it exists
) {
    val context = LocalContext.current // Get the context to access dataStore
    val scope = rememberCoroutineScope() // Create a coroutine scope to launch async tasks

    var quantity by remember { mutableStateOf("") } // State for the quantity input
    var isBorrowed by remember { mutableStateOf(false) } // State for the type (Sold/Borrowed)

    // Preload previous values for new entries if no initial entry is provided
    LaunchedEffect(initialEntry) {
        if (initialEntry == null) {
            // If it's a new entry, load saved quantity and isBorrowed values from dataStore
            val prefs = context.dataStore.data.first()
            quantity = prefs[LAST_QUANTITY] ?: "" // Default to empty string if not found
            isBorrowed = prefs[LAST_IS_BORROWED] ?: false // Default to false if not found
        } else {
            // If an existing entry is provided, prefill with its values
            quantity = initialEntry.quantity.toString()
            isBorrowed = initialEntry.isBorrowed
        }
    }

    // Display the AlertDialog to capture milk entry details
    AlertDialog(
        onDismissRequest = onDismiss, // Close the dialog when dismissed
        confirmButton = {
            // Save button logic
            TextButton(onClick = {
                // Convert quantity to double and save if valid
                val qty = quantity.toDoubleOrNull()
                if (qty != null) {
                    val newEntry = MilkEntry(
                        id = initialEntry?.id ?: 0, // Use existing ID or create a new one
                        date = date, // Use the date passed to the dialog
                        quantity = qty, // The entered quantity
                        isBorrowed = isBorrowed // The type (Sold or Borrowed)
                    )
                    onSave(newEntry) // Call the onSave callback to save the entry

                    // Save these values for future new entries only
                    scope.launch {
                        context.dataStore.edit { prefs ->
                            prefs[LAST_QUANTITY] = quantity
                            prefs[LAST_IS_BORROWED] = isBorrowed
                        }
                    }
                }
                onDismiss() // Dismiss the dialog after saving
            }) {
                Text("Save") // Text for the save button
            }
        },
        dismissButton = {
            // Cancel/Delete button logic
            Row {
                if (initialEntry != null) {
                    // If there is an existing entry, show delete button
                    TextButton(
                        onClick = {
                            onDelete?.invoke() // Invoke the onDelete callback
                            // Optionally clear saved defaults if this entry is deleted
                            scope.launch {
                                context.dataStore.edit { prefs ->
                                    prefs.remove(LAST_QUANTITY)
                                    prefs.remove(LAST_IS_BORROWED)
                                }
                            }
                            onDismiss() // Dismiss the dialog after deletion
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error) // Text for delete button
                    }
                }
                Spacer(modifier = Modifier.width(8.dp)) // Space between buttons
                TextButton(onClick = onDismiss) {
                    Text("Cancel") // Text for cancel button
                }
            }
        },
        title = {
            Text("Milk Entry for ${date.dayOfMonth}/${date.monthValue}") // Title of the dialog, showing the date
        },
        text = {
            // The main content of the dialog
            Card(
                modifier = Modifier
                    .fillMaxWidth() // Make card fill the available width
                    .padding(6.dp), // Add padding around the card
                shape = MaterialTheme.shapes.medium, // Use a rounded shape for the card
            ) {
                // Column inside the card
                Column(modifier = Modifier.padding(6.dp)) {
                    // Quantity Input Field
                    OutlinedTextField(
                        value = quantity, // Current value of quantity
                        onValueChange = { quantity = it }, // Update the value when changed
                        label = { Text("Quantity (Litres)") }, // Label for the text field
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number), // Set keyboard type to Number
                        modifier = Modifier.fillMaxWidth() // Make the text field fill the available width
                    )
                    Spacer(modifier = Modifier.height(16.dp)) // Space between fields

                    // Type of entry selection (Sold/Borrowed) using Toggle Buttons
                    Text("Type:", style = MaterialTheme.typography.bodyMedium) // Label for type selection
                    Spacer(modifier = Modifier.height(8.dp)) // Space between label and buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(), // Fill the available width
                        horizontalArrangement = Arrangement.SpaceAround // Space out the buttons
                    ) {
                        // Toggle Button for "Sold"
                        Button(
                            onClick = { isBorrowed = false }, // Set isBorrowed to false (Sold)
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isBorrowed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            ), // Change the color based on the selection
                            modifier = Modifier.weight(1f) // Make the button take equal space
                        ) {
                            Text("Sold") // Text for the button
                        }
                        Spacer(modifier = Modifier.width(4.dp)) // Space between buttons
                        // Toggle Button for "Borrowed"
                        Button(
                            onClick = { isBorrowed = true }, // Set isBorrowed to true (Borrowed)
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBorrowed) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                            ), // Change the color based on the selection
                            modifier = Modifier.weight(1f) // Make the button take equal space
                        ) {
                            Text("Borrowed") // Text for the button
                        }
                    }
                }
            }
        }
    )
}
