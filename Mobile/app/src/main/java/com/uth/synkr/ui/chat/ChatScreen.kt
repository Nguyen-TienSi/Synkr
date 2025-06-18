package com.uth.synkr.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uth.synkr.R
import com.uth.synkr.utils.InputStringHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object TriangleShape {
    val Left = GenericShape { size, _ ->
        moveTo(0f, size.height / 2)
        lineTo(size.width, 0f)
        lineTo(size.width, size.height)
        close()
    }

    val Right = GenericShape { size, _ ->
        moveTo(size.width, size.height / 2)
        lineTo(0f, 0f)
        lineTo(0f, size.height)
        close()
    }
}

@Composable
fun ChatContent(
    text: String,
    isFromUser: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
    ) {
        if (!isFromUser) {
            Box(
                modifier = Modifier
                    .weight(0.14f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ffff_222),
                    contentDescription = "Ảnh đại diện",
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .border(1.dp, Color.Gray, shape = CircleShape)
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(0.7f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (!isFromUser) Arrangement.Start else Arrangement.End
            ) {
                if (!isFromUser) {

                    Box(
                        modifier = Modifier
                            .size(10.dp, 10.dp)
                            .align(Alignment.Top)
                            .background(Color(0xFFE0E0E0), TriangleShape.Left)
                    )
                }

                Box(
                    modifier = Modifier
                        .widthIn(max = 240.dp)
                        .background(
                            color = if (isFromUser) Color(0xFFDCF8C6) else Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(
                                topStart = if (isFromUser) 16.dp else 0.dp,
                                topEnd = if (isFromUser) 0.dp else 16.dp,
                                bottomEnd = 16.dp,
                                bottomStart = 16.dp
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),

                    ) {
                    Text(
                        text = text,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }

                if (isFromUser) {
                    Box(
                        modifier = Modifier
                            .size(10.dp, 10.dp)
                            .align(Alignment.Top)
                            .background(Color(0xFFDCF8C6), TriangleShape.Right)
                    )
                }
            }
        }

        if (isFromUser) {
            Box(
                modifier = Modifier
                    .weight(0.14f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ffff_222),
                    contentDescription = "Ảnh đại diện",
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .border(1.dp, Color.Gray, shape = CircleShape)

                )
            }
        }
    }

}

@Composable
fun MenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(64.dp)
            .clickable(onClick = onClick)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 1.dp,
            shadowElevation = 4.dp,
            modifier = Modifier.size(56.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF333333),
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun IconGridMenu() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFDFDFD)) // Nền tổng thể sáng
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MenuItem(icon = Icons.Default.Photo, label = "Album")
            MenuItem(icon = Icons.Default.CameraAlt, label = "Camera")
            MenuItem(icon = Icons.Default.Place, label = "Vị trí")
            MenuItem(icon = Icons.Default.Favorite, label = "Ưa thích")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MenuItem(icon = Icons.Default.Person, label = "Thẻ liên hệ")
            MenuItem(icon = Icons.Default.Folder, label = "Tập tin")
            MenuItem(icon = Icons.Default.MusicNote, label = "Âm nhạc")
            Spacer(modifier = Modifier.size(64.dp)) // Giữ cân bằng lưới
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(userName: String, onBackClick: (() -> Unit)? = null) {
    var text by remember { mutableStateOf("") }
    var sendOrLike by remember { mutableStateOf(Icons.Default.ThumbUp) }

    val itemsList = remember { mutableStateListOf<String>() }
    LaunchedEffect(Unit) {
        repeat(30) {
            itemsList.add("Ite... $it")
        }
    }

    val listState = rememberLazyListState()
    var showMenu by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val coroutineScope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Press) {
                showMenu = false
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        val strokeWidth = 1.dp.toPx()
                        drawLine(
                            color = Color.LightGray,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = strokeWidth
                        )
                    },
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onBackClick?.invoke() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: xử lý menu */ }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    actionIconContentColor = Color.Black,
                    titleContentColor = Color.Black,
                    containerColor = Color.White
                )
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    reverseLayout = true,
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    var i = 1
                    items(itemsList.reversed()) { item ->
                        i++
                        Spacer(modifier = Modifier.height(5.dp))
                        ChatContent(item, i % 2 == 0)
                    }
                }
            }
        },
        bottomBar = {
            Column {
                Surface(
                    tonalElevation = 12.dp,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .imePadding()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp, top = 12.dp, bottom = 25.dp)
                            .wrapContentHeight()
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        IconButton(
                            onClick = { /* Handle mic click */ },
                            modifier = Modifier
                                .border(
                                    width = 3.dp,
                                    shape = CircleShape,
                                    color = Color.Black
                                )
                        ) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = "Mic")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            value = text,
                            onValueChange = {
                                text = InputStringHandler.checkMess(it)
                                sendOrLike = InputStringHandler.updateIconAccordingToText()
                            },
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .weight(1f),
                            singleLine = false,
                            maxLines = 5,
                            interactionSource = interactionSource


                        )

                        Spacer(modifier = Modifier.width(2.dp))

                        IconButton(onClick = {
                            coroutineScope.launch {
                                keyboardController?.hide()
                                delay(100)
                                showMenu = !showMenu
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.AddCircleOutline,
                                contentDescription = "Add",
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(2.dp))
                        IconButton(onClick = {
                            if (sendOrLike == Icons.AutoMirrored.Filled.Send) {
                                itemsList.add(text)
                            } else {
                                itemsList.add("👍")
                            }
                        }) {
                            Icon(
                                imageVector = sendOrLike,
                                contentDescription = "Show icons",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
                if (showMenu) {
                    Surface(
                        tonalElevation = 10.dp,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .imePadding()
                            .drawBehind {
                                drawLine(
                                    color = Color.Gray,
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, 0f),
                                    strokeWidth = 2.dp.toPx()
                                )
                            }
                    ) {
                        IconGridMenu()
                    }
                }

            }
        }

    )

}

@Preview(showBackground = true)
@Composable
fun ChatAppBarPreview() {
    ChatScreen("ekegggggggggggggggkek", null)
}
