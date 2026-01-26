package com.owais.milktracker.ui.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Simple, clean Material 3 onboarding scaffold.
 * No effects. No gradients. No hacks.
 */
@Composable
fun OnboardingScaffold(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
    content: @Composable (() -> Unit)? = null,
    showBack: Boolean,
    primaryButton: String,
    onBack: (() -> Unit)? = null,
    onPrimaryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        // ───── Center Content ─────
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            icon()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            content?.let {
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        it()
                    }
                }
            }
        }

        // ───── Bottom Navigation ─────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (showBack) {
                OutlinedButton(onClick = { onBack?.invoke() }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }

            Button(onClick = onPrimaryClick) {
                when (primaryButton) {

                    "Allow" -> {
                        Text("Allow")
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Allow notifications"
                        )
                    }

                    else -> {
                        Text(primaryButton)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = "Next"
                        )
                    }
                }
            }
        }
    }
}
