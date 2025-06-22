package com.uth.synkr.ui.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.uth.synkr.R

@Composable
fun NavDrawer(
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    userName: String,
    userEmail: String,
    userPhotoUrl: String? = null
) {
    Column(
        modifier = Modifier
            .width(260.dp)
            .fillMaxHeight()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A47A1))
                .padding(bottom = 24.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            if (userPhotoUrl != null) {
                // Use Coil or Glide for loading network images
                Image(
                    painter = rememberAsyncImagePainter(userPhotoUrl),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.sample_profile_pic),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = userName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp
            )
            Text(
                text = userEmail, color = Color.White, fontSize = 15.sp
            )
        }
        // Navigation Items
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            DrawerItem(
                icon = Icons.AutoMirrored.Filled.Chat,
                label = "Home",
                selected = selectedItem == "Home",
                selectedColor = Color(0xFF2962FF),
                onClick = { onItemSelected("Home") })
            DrawerItem(
                icon = Icons.Filled.Group,
                label = "Contacts",
                selected = selectedItem == "Contacts",
                onClick = { onItemSelected("Contacts") })
            DrawerItem(
                icon = Icons.Filled.Search,
                label = "Search",
                selected = selectedItem == "Search",
                onClick = { onItemSelected("Search") })
            DrawerItem(
                icon = Icons.Filled.Person,
                label = "Profile",
                selected = selectedItem == "Profile",
                onClick = { onItemSelected("Profile") })
        }
    }
}

@Composable
private fun DrawerItem(
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
            .clickable { onClick() }, // Make row clickable
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
