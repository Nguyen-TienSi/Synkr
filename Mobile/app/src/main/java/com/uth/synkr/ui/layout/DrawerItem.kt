package com.uth.synkr.ui.layout

import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DrawerItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    selectedColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 8.dp, bottom = 8.dp)
            .background(if (selected) Color(0x112962FF) else Color.Transparent)
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) selectedColor else Color.Black,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = label,
            color = if (selected) selectedColor else Color.Black,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 18.sp
        )
    }
}
