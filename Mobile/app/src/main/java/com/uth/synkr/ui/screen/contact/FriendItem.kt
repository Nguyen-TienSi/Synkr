package com.uth.synkr.ui.screen.contact

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.uth.synkr.data.model.User
import com.uth.synkr.data.model.enumeration.FriendshipStatus

@Composable
fun FriendItem(
    user: User,
    friendshipStatus: FriendshipStatus?,
    isRequester: Boolean = false,
    isAddressee: Boolean = false,
    onAddFriend: () -> Unit,
    onAccept: () -> Unit = {},
    onUnfriend: () -> Unit = {},
    onCancel: () -> Unit = {},
    onReject: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(user.pictureUrl),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray, CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = user.fullName, modifier = Modifier.weight(1f)
        )
        when (friendshipStatus) {
            FriendshipStatus.ACCEPTED -> {
                FriendshipActionButton(
                    action = FriendshipAction.UNFRIEND, onClick = onUnfriend
                )
            }

            FriendshipStatus.PENDING -> {
                if (isAddressee) {
                    FriendshipActionButton(
                        action = FriendshipAction.ACCEPT, onClick = onAccept
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FriendshipActionButton(
                        action = FriendshipAction.REJECT, onClick = onReject
                    )
                } else if (isRequester) {
                    FriendshipActionButton(
                        action = FriendshipAction.CANCEL, onClick = onCancel
                    )
                }
            }

            else -> {
                FriendshipActionButton(
                    action = FriendshipAction.ADD, onClick = onAddFriend
                )
            }
        }
    }
}
