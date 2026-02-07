package com.owais.milktracker.ui.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.owais.milktracker.viewmodel.MilkViewModel
import kotlinx.coroutines.flow.map
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarDay(
    date: LocalDate,
    viewModel: MilkViewModel,
    onClick: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val isFuture = date.isAfter(today)

    val entry by viewModel.entries
        .map { it[date] }
        .collectAsState(initial = null)

    val bgColor = when {
        isFuture -> Color.Transparent
        entry?.isBorrowed == true -> Color.Red.copy(alpha = 0.15f)
        entry?.isBorrowed == false -> Color.Blue.copy(alpha = 0.15f)
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(bgColor, shape = MaterialTheme.shapes.small)
            .clickable(enabled = !isFuture) {
                onClick(date)
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${date.dayOfMonth}",
                color = if (isFuture) Color.Gray else Color.Unspecified,
                style = MaterialTheme.typography.labelLarge
            )

            if (entry != null && !isFuture) {
                Text("${entry!!.quantity}L", style = MaterialTheme.typography.labelSmall)
                Icon(
                    imageVector = if (entry!!.isBorrowed)
                        Icons.AutoMirrored.Filled.TrendingDown
                    else
                        Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    tint = if (entry!!.isBorrowed) Color.Red else Color.Blue,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
