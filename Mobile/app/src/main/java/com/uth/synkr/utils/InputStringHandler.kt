package com.uth.synkr.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector

class InputStringHandler {
    companion object {
        private val sendIcon: ImageVector = Icons.Default.Send
        private val moreIcon: ImageVector = Icons.Default.ThumbUp

        private var textInput: String? = null

        fun checkMess(input: String): String {
            return if (input.trim().isEmpty()) {
                textInput = ""
                ""
            }
            else {
                textInput = input
                input
            }
        }
        fun updateIconAccordingToText(): ImageVector{
            return if (textInput.isNullOrEmpty()){
                moreIcon
            } else {
                sendIcon
            }
        }
    }
}