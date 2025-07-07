package com.uth.synkr.ui.screen.auth

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PasswordRequirement(met: Boolean, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = if (met) Icons.Filled.Check else Icons.Filled.Close,
            contentDescription = null,
            tint = if (met) Color.Green else Color.Gray,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (met) Color.Green else Color.Gray
        )
    }
}
