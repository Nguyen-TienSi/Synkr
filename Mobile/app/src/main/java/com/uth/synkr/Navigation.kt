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

    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf("Gợi ý", "Lời mời", "Bạn bè").forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Filled.PersonAdd
                                    1 -> Icons.Filled.Notifications
                                    else -> Icons.Filled.People
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
                0 -> SuggestedFriendsScreen(dummyUsers, {}, {})
                1 -> FriendRequestsScreen(dummyUsers, {}, {})
                2 -> FriendsListScreen(dummyUsers, {}, {})
            }
        }
    }
}
