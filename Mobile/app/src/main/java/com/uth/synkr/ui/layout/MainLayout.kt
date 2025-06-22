package com.uth.synkr.ui.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainLayout(
    title: String, onMenuClick: () -> Unit = {}, content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        MainHeader(
            title = title, onMenuClick = onMenuClick
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp), content = content
        )
    }
}
