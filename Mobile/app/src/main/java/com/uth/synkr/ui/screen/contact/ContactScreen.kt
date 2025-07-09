package com.uth.synkr.ui.screen.contact

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uth.synkr.data.model.enumeration.FriendshipStatus

@Composable
fun ContactScreen(currentUserId: String) {
    var query by remember { mutableStateOf("") }
    val viewModel: FriendListViewModel = viewModel { FriendListViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    val optimisticStates by viewModel.optimisticFriendshipStates.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(currentUserId) {
        viewModel.loadFriends(currentUserId)
    }

    // Show error snackbar
    LaunchedEffect(uiState.friendsError) {
        uiState.friendsError?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearErrors()
        }
    }

    LaunchedEffect(uiState.searchError) {
        uiState.searchError?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearErrors()
        }
    }

    LaunchedEffect(uiState.actionError) {
        uiState.actionError?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearErrors()
        }
    }

    val usersToDisplay = if (query.isNotBlank() && uiState.searchResults.isNotEmpty()) {
        uiState.searchResults
    } else {
        uiState.friends
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            SearchBar(
                query = query,
                onQueryChange = { newQuery ->
                    query = newQuery
                    viewModel.searchUsers(newQuery, currentUserId)
                }
            )
            
            when {
                uiState.isLoadingFriends && uiState.friends.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                uiState.isSearching && query.isNotBlank() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                usersToDisplay.isEmpty() && !uiState.isLoadingFriends && !uiState.isSearching -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (query.isNotBlank()) {
                                "No users found for \"$query\""
                            } else {
                                "No friends yet\nSearch for users to add as friends"
                            },
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                else -> {
                    LazyColumn {
                        items(usersToDisplay) { user ->
                            // Check for optimistic state first
                            val optimisticState = optimisticStates[user.uid]
                            
                            if (optimisticState != null) {
                                // Use optimistic state immediately - no API calls at all
                                FriendItem(
                                    user = user,
                                    friendshipStatus = optimisticState.status,
                                    isRequester = optimisticState.isRequester,
                                    isAddressee = optimisticState.isAddressee,
                                    onAddFriend = { viewModel.addFriend(currentUserId, user.uid) },
                                    onAccept = { viewModel.acceptRequest(currentUserId, user.uid) },
                                    onUnfriend = { viewModel.unfriend(currentUserId, user.uid) },
                                    onCancel = { viewModel.cancelRequest(currentUserId, user.uid) },
                                    onReject = { viewModel.rejectRequest(currentUserId, user.uid) },
                                    isLoading = uiState.isProcessingAction
                                )
                            } else {
                                // Only fetch API state if no optimistic state exists
                                val friendshipStatusLiveData = viewModel.getFriendshipStatus(currentUserId, user.uid)
                                val friendshipStatus by friendshipStatusLiveData.observeAsState()
                                var isRequester: Boolean? by remember { mutableStateOf(null) }
                                var isAddressee: Boolean? by remember { mutableStateOf(null) }

                                // Only fetch requester/addressee info if no optimistic state and status is PENDING
                                LaunchedEffect(friendshipStatus) {
                                    // Double-check that no optimistic state exists before proceeding
                                    if (friendshipStatus == FriendshipStatus.PENDING && optimisticStates[user.uid] == null) {
                                        viewModel.isRequester(currentUserId, user.uid) { result ->
                                            // Only update if still no optimistic state
                                            if (optimisticStates[user.uid] == null) {
                                                isRequester = result
                                                isAddressee = result == false
                                            }
                                        }
                                    } else if (friendshipStatus != FriendshipStatus.PENDING) {
                                        isRequester = false
                                        isAddressee = false
                                    }
                                }

                                // Additional check to prevent rendering if optimistic state appeared
                                if (optimisticStates[user.uid] == null) {
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
                                        isLoading = uiState.isProcessingAction
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
