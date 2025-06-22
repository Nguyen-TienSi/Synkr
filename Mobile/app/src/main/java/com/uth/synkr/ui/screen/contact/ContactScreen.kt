package com.uth.synkr.ui.screen.contact

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun ContactScreen() {
    var query by remember { mutableStateOf("") }

    Column {
        SearchBar(
            query = query, onQueryChange = { query = it })
    }
}
