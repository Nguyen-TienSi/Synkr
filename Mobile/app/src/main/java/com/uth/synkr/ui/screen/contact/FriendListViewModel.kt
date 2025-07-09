package com.uth.synkr.ui.screen.contact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.synkr.data.model.Friendship
import com.uth.synkr.data.model.User
import com.uth.synkr.data.model.enumeration.FriendshipStatus
import com.uth.synkr.data.service.FriendshipService
import com.uth.synkr.data.service.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FriendListViewModel(
    private val userService: UserService = UserService(),
    private val friendshipService: FriendshipService = FriendshipService()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactUiState())
    val uiState: StateFlow<ContactUiState> = _uiState.asStateFlow()

    // Keep existing LiveData for compatibility
    private val _friends = MutableLiveData<List<User>>(emptyList())
    val friends: LiveData<List<User>> = _friends
    private val _searchResults = MutableLiveData<List<User>>(emptyList())
    val searchResults: LiveData<List<User>> = _searchResults
    
    // Optimistic state management for friendship status
    private val _optimisticFriendshipStates = MutableStateFlow<Map<String, OptimisticFriendshipState>>(emptyMap())
    val optimisticFriendshipStates: StateFlow<Map<String, OptimisticFriendshipState>> = _optimisticFriendshipStates.asStateFlow()

    fun loadFriends(currentUserId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingFriends = true, friendsError = null)
            try {
                val friendshipStatusList = listOf(
                    FriendshipStatus.ACCEPTED,
                    FriendshipStatus.PENDING
                )
                val users = userService.getFriendsOfUser(currentUserId, friendshipStatusList)
                _friends.value = users
                _uiState.value = _uiState.value.copy(
                    friends = users,
                    isLoadingFriends = false,
                    friendsError = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingFriends = false,
                    friendsError = e.message ?: "Failed to load friends"
                )
            }
        }
    }

    fun searchUsers(query: String, currentUserId: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _uiState.value = _uiState.value.copy(
                searchResults = emptyList(),
                isSearching = false,
                searchError = null
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true, searchError = null)
            try {
                val results = userService.searchUsers(query, currentUserId)
                _searchResults.value = results
                _uiState.value = _uiState.value.copy(
                    searchResults = results,
                    isSearching = false,
                    searchError = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    searchError = e.message ?: "Failed to search users"
                )
            }
        }
    }

    fun addFriend(currentUserId: String, addresseeId: String) {
        // Optimistic update - set immediately before any API calls
        updateOptimisticState(addresseeId, OptimisticFriendshipState(
            status = FriendshipStatus.PENDING,
            isRequester = true,
            isAddressee = false
        ))
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessingAction = true, actionError = null)
            try {
                friendshipService.createFriendship(
                    Friendship(
                        requesterId = currentUserId, addresseeId = addresseeId
                    )
                )
                // Remove optimistic state after successful API call
                removeOptimisticState(addresseeId)
                loadFriends(currentUserId)
                _uiState.value = _uiState.value.copy(isProcessingAction = false)
            } catch (e: Exception) {
                // Revert optimistic state on error
                removeOptimisticState(addresseeId)
                _uiState.value = _uiState.value.copy(
                    isProcessingAction = false,
                    actionError = e.message ?: "Failed to send friend request"
                )
            }
        }
    }

    fun acceptRequest(currentUserId: String, requesterId: String) {
        // Optimistic update - set immediately before any API calls
        updateOptimisticState(requesterId, OptimisticFriendshipState(
            status = FriendshipStatus.ACCEPTED,
            isRequester = false,
            isAddressee = false
        ))
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessingAction = true, actionError = null)
            try {
                friendshipService.acceptRequest(currentUserId, requesterId)
                removeOptimisticState(requesterId)
                loadFriends(currentUserId)
                _uiState.value = _uiState.value.copy(isProcessingAction = false)
            } catch (e: Exception) {
                removeOptimisticState(requesterId)
                _uiState.value = _uiState.value.copy(
                    isProcessingAction = false,
                    actionError = e.message ?: "Failed to accept friend request"
                )
            }
        }
    }

    fun rejectRequest(currentUserId: String, requesterId: String) {
        // Optimistic update - remove from list immediately
        updateOptimisticState(requesterId, OptimisticFriendshipState(
            status = null,
            isRequester = false,
            isAddressee = false
        ))
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessingAction = true, actionError = null)
            try {
                friendshipService.rejectRequest(currentUserId, requesterId)
                removeOptimisticState(requesterId)
                loadFriends(currentUserId)
                _uiState.value = _uiState.value.copy(isProcessingAction = false)
            } catch (e: Exception) {
                removeOptimisticState(requesterId)
                _uiState.value = _uiState.value.copy(
                    isProcessingAction = false,
                    actionError = e.message ?: "Failed to reject friend request"
                )
            }
        }
    }

    fun unfriend(currentUserId: String, friendId: String) {
        // Optimistic update - remove from list immediately
        updateOptimisticState(friendId, OptimisticFriendshipState(
            status = null,
            isRequester = false,
            isAddressee = false
        ))
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessingAction = true, actionError = null)
            try {
                friendshipService.unfriend(currentUserId, friendId)
                removeOptimisticState(friendId)
                loadFriends(currentUserId)
                _uiState.value = _uiState.value.copy(isProcessingAction = false)
            } catch (e: Exception) {
                removeOptimisticState(friendId)
                _uiState.value = _uiState.value.copy(
                    isProcessingAction = false,
                    actionError = e.message ?: "Failed to unfriend"
                )
            }
        }
    }

    fun cancelRequest(currentUserId: String, requesterId: String) {
        // Optimistic update - remove from list immediately
        updateOptimisticState(requesterId, OptimisticFriendshipState(
            status = null,
            isRequester = false,
            isAddressee = false
        ))
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessingAction = true, actionError = null)
            try {
                friendshipService.cancelRequest(currentUserId, requesterId)
                removeOptimisticState(requesterId)
                loadFriends(currentUserId)
                _uiState.value = _uiState.value.copy(isProcessingAction = false)
            } catch (e: Exception) {
                removeOptimisticState(requesterId)
                _uiState.value = _uiState.value.copy(
                    isProcessingAction = false,
                    actionError = e.message ?: "Failed to cancel request"
                )
            }
        }
    }

    private fun updateOptimisticState(userId: String, state: OptimisticFriendshipState) {
        // Update immediately in the same thread to prevent any delay
        _optimisticFriendshipStates.value = _optimisticFriendshipStates.value.toMutableMap().apply {
            put(userId, state)
        }
    }

    private fun removeOptimisticState(userId: String) {
        _optimisticFriendshipStates.value = _optimisticFriendshipStates.value.toMutableMap().apply {
            remove(userId)
        }
    }

    fun getFriendshipStatus(currentUserId: String, friendId: String): LiveData<FriendshipStatus?> {
        val liveData = MutableLiveData<FriendshipStatus?>()
        
        // Check if we have an optimistic state first
        val optimisticState = _optimisticFriendshipStates.value[friendId]
        if (optimisticState != null) {
            // Return optimistic state immediately
            liveData.value = optimisticState.status
            return liveData
        }
        
        // Only fetch from API if no optimistic state exists
        viewModelScope.launch {
            val status = friendshipService.getFriendshipStatus(currentUserId, friendId)
            // Only update if still no optimistic state exists
            if (_optimisticFriendshipStates.value[friendId] == null) {
                liveData.value = status
            }
        }
        return liveData
    }

    fun isRequester(currentUserId: String, friendId: String, callback: (Boolean?) -> Unit) {
        viewModelScope.launch {
            val friendship = friendshipService.getFriendship(currentUserId, friendId)
            callback(friendship?.requesterId == currentUserId)
        }
    }
    
    fun clearErrors() {
        _uiState.value = _uiState.value.copy(
            friendsError = null,
            searchError = null,
            actionError = null
        )
    }
}

data class ContactUiState(
    val friends: List<User> = emptyList(),
    val searchResults: List<User> = emptyList(),
    val isLoadingFriends: Boolean = false,
    val isSearching: Boolean = false,
    val isProcessingAction: Boolean = false,
    val friendsError: String? = null,
    val searchError: String? = null,
    val actionError: String? = null
)

data class OptimisticFriendshipState(
    val status: FriendshipStatus?,
    val isRequester: Boolean,
    val isAddressee: Boolean
)
