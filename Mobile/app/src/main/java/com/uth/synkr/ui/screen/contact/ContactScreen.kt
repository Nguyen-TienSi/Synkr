package com.uth.synkr.ui.screen.contact

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uth.synkr.data.model.enumeration.FriendshipStatus

@Composable
fun ContactScreen(currentUserId: String) {
    var query by remember { mutableStateOf("") }
    val viewModel: FriendListViewModel = viewModel()
    val searchResults by viewModel.searchResults.observeAsState(emptyList())
    val friends by viewModel.friends.observeAsState(emptyList())

    LaunchedEffect(currentUserId) {
        viewModel.loadFriends(currentUserId)
    }

    val usersToDisplay = if (query.isNotBlank() && searchResults.isNotEmpty()) {
        searchResults
    } else {
        friends
    }

    Column {
        SearchBar(
            query = query, onQueryChange = {
                query = it
                viewModel.searchUsers(query, currentUserId)
            })
        LazyColumn {
            items(usersToDisplay) { user ->
                val friendshipStatusLiveData =
                    viewModel.getFriendshipStatus(currentUserId, user.uid)
                val friendshipStatus by friendshipStatusLiveData.observeAsState()
                var isRequester: Boolean? by remember { mutableStateOf(null) }
                var isAddressee: Boolean? by remember { mutableStateOf(null) }

                LaunchedEffect(friendshipStatus) {
                    if (friendshipStatus == FriendshipStatus.PENDING) {
                        viewModel.isRequester(currentUserId, user.uid) { result ->
                            isRequester = result
                            isAddressee = result == false
                        }
                    } else {
                        isRequester = false
                        isAddressee = false
                    }
                }

                FriendItem(
                    user = user,
                    friendshipStatus = friendshipStatus,
                    isRequester = isRequester == true,
                    isAddressee = isAddressee == true,
                    onAddFriend = { viewModel.addFriend(currentUserId, user.uid) },
                    onAccept = { viewModel.acceptRequest(currentUserId, user.uid) },
                    onUnfriend = { viewModel.unfriend(currentUserId, user.uid) },
                    onCancel = { viewModel.cancelRequest(currentUserId, user.uid) },
                    onReject = { viewModel.rejectRequest(currentUserId, user.uid) },
                )
            }
        }
    }
}
