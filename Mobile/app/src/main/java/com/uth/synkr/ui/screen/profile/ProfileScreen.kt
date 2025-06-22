package com.uth.synkr.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uth.synkr.R

@Composable
fun ProfileScreen(
    userName: String = "Cristina Kardashian",
    userEmail: String = "cristina@gmail.com",
    onLogout: () -> Unit = {},
    onAccountDetails: () -> Unit = {},
    onSettings: () -> Unit = {},
    onContactUs: () -> Unit = {},
    onChangeAvatar: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Picture with Edit Button
        Box(
            modifier = Modifier
                .size(120.dp)
                .padding(top = 16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Image(
                painter = painterResource(id = R.drawable.sample_profile_pic),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
            )
            IconButton(
                onClick = onChangeAvatar,
                modifier = Modifier
                    .offset(x = (-8).dp, y = (-8).dp)
                    .size(36.dp)
                    .background(Color(0xFF2962FF), CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.sample_profile_pic),
                    contentDescription = "Change Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = userName, fontSize = 22.sp, color = Color.Black
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Menu Items
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            ProfileMenuItem(
                icon = Icons.Filled.AccountCircle,
                text = "Account Details",
                onClick = onAccountDetails
            )
            ProfileMenuItem(
                icon = Icons.Filled.Settings, text = "Settings", onClick = onSettings
            )
            ProfileMenuItem(
                icon = Icons.Filled.Call,
                text = "Contact Us",
                onClick = onContactUs,
                iconTint = Color(0xFF4CAF50),
                textColor = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Logout Button
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Logout", color = Color.Black, fontSize = 16.sp
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    iconTint: Color = Color(0xFF2962FF),
    textColor: Color = Color.Black
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text, color = textColor, fontSize = 16.sp
        )
    }
}

