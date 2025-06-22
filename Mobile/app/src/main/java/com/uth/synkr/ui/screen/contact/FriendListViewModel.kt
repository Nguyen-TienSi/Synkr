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
import kotlinx.coroutines.launch

class FriendListViewModel(
    private val userService: UserService = UserService(),
    private val friendshipService: FriendshipService = FriendshipService()
) : ViewModel() {

    private val _friends = MutableLiveData<List<User>>(emptyList())
    val friends: LiveData<List<User>> = _friends
    private val _searchResults = MutableLiveData<List<User>>(emptyList())
    val searchResults: LiveData<List<User>> = _searchResults
    private val friendshipStatusMap = mutableMapOf<Pair<String, String>, MutableLiveData<FriendshipStatus?>>()

    fun loadFriends(currentUserId: String) {
        viewModelScope.launch {
            val friendshipStatusList = listOf(
                FriendshipStatus.ACCEPTED,
                FriendshipStatus.PENDING
            )
            val users = userService.getFriendsOfUser(currentUserId, friendshipStatusList)
            _friends.value = users
        }
    }

    fun addFriend(currentUserId: String, addresseeId: String) {
        viewModelScope.launch {
            friendshipService.createFriendship(
                Friendship(
                    requesterId = currentUserId, addresseeId = addresseeId
                )
            )
            loadFriends(currentUserId)
        }
    }

    fun acceptRequest(currentUserId: String, requesterId: String) {
        viewModelScope.launch {
            friendshipService.acceptRequest(currentUserId, requesterId)
            loadFriends(currentUserId)
        }
    }

    fun rejectRequest(currentUserId: String, requesterId: String) {
        viewModelScope.launch {
            friendshipService.rejectRequest(currentUserId, requesterId)
            loadFriends(currentUserId)
        }
    }

    fun unfriend(currentUserId: String, friendId: String) {
        viewModelScope.launch {
            friendshipService.unfriend(currentUserId, friendId)
            loadFriends(currentUserId)
        }
    }

    fun cancelRequest(currentUserId: String, requesterId: String) {
        viewModelScope.launch {
            friendshipService.cancelRequest(currentUserId, requesterId)
            loadFriends(currentUserId)
        }
    }

    fun getFriendshipStatus(currentUserId: String, friendId: String): LiveData<FriendshipStatus?> {
        val key = currentUserId to friendId
        val liveData = friendshipStatusMap.getOrPut(key) { MutableLiveData() }
        viewModelScope.launch {
            val status = friendshipService.getFriendshipStatus(currentUserId, friendId)
            liveData.value = status
        }
        return liveData
    }

    fun searchUsers(query: String, currentUserId: String) {
        viewModelScope.launch {
            val results = userService.searchUsers(query, currentUserId)
            _searchResults.value = results
        }
    }

    fun isRequester(currentUserId: String, friendId: String, callback: (Boolean?) -> Unit) {
        viewModelScope.launch {
            val friendship = friendshipService.getFriendship(currentUserId, friendId)
            callback(friendship?.requesterId == currentUserId)
        }
    }
}
