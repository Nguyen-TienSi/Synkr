package com.uth.synkr.utils


sealed class MessBox {
    data class Text(val content: String) : MessBox()
    data class Image(val url: String) : MessBox()
    data class Video(val url: String) : MessBox()
    data class Audio(val url: String) : MessBox()
}