package com.uth.synkr

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.uth.synkr.model.User
import com.uth.synkr.ui.screens.*
import androidx.compose.material.icons.filled.Search

@Composable
fun FriendAppScreen() {
    val dummyUsers = remember {
        listOf(
            User("1", "Peter", "https://randomuser.me/api/portraits/men/1.jpg"),
            User("2", "Lucy", "https://randomuser.me/api/portraits/women/2.jpg"),
            User("3", "Tom", "https://randomuser.me/api/portraits/men/3.jpg")
        )
    }

    var selectedTab by remember { mutableStateOf(0) }

    val tabTitles = listOf("Danh bạ", "Tìm kiếm", "Kết bạn")

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabTitles.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Filled.People      // Danh bạ
                                    1 -> Icons.Filled.Search      // Tìm kiếm
                                    else -> Icons.Filled.PersonAdd // Kết bạn
                                },
                                contentDescription = title
                            )
                        },
                        label = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> FriendsListScreen(dummyUsers, {}, {})         // Danh bạ
                1 -> SuggestedFriendsScreen(dummyUsers, {}, {})    // Tìm kiếm
                2 -> FriendRequestsScreen(dummyUsers, {}, {})      // Kết bạn
            }
        }
    }
}

