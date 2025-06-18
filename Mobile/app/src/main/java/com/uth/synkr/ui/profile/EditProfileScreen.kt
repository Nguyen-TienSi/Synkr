package com.uth.synkr.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt // Cho biểu tượng camera
import androidx.compose.material.icons.filled.KeyboardArrowDown // Cho dropdown
import androidx.compose.material3.* // Ensures you are using Material 3 components
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource // Để dùng painterResource cho ảnh đại diện
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uth.synkr.R
import com.uth.synkr.ui.theme.Synkr1562Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit // Callback để quay lại màn hình trước
) {
    // Trạng thái cho các trường nhập liệu
    var name by remember { mutableStateOf("Melissa Peters") } // Cập nhật tên theo ảnh
    var email by remember { mutableStateOf("melpeters@gmail.com") } // Cập nhật email theo ảnh
    var password by remember { mutableStateOf("**********") }
    var dateOfBirth by remember { mutableStateOf("23/05/1995") } // Giá trị mặc định
    // var countryRegion by remember { mutableStateOf("Nigeria") } // Đã loại bỏ

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Thêm padding cho thanh trên cùng
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start // Căn về bên trái
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(16.dp)) // Khoảng cách giữa icon và tiêu đề
            Text(
                text = "Edit Profile",
                fontSize = 24.sp, // Nhỏ hơn một chút so với Settings để phù hợp
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f) // Chiếm không gian còn lại
            )
        }
        Spacer(modifier = Modifier.height(24.dp)) // Khoảng cách giữa top bar và ảnh đại diện

        // Profile Picture with Camera Icon
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 32.dp)
                .wrapContentSize(align = Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD3D3D3)) // Màu nền mặc định
            ) {
                // Sử dụng ảnh đại diện mẫu tương tự màn hình Settings
                Image(
                    painter = painterResource(id = R.drawable.sample_profile_pic), // Đảm bảo bạn có ảnh này
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Biểu tượng camera ở góc dưới bên phải ảnh đại diện
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Change Profile Picture",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 8.dp, y = 8.dp) // Dịch chuyển ra ngoài một chút
                    .size(40.dp) // Kích thước lớn hơn một chút
                    .clip(CircleShape)
                    .background(Color(0xFF6200EE)) // Màu tím hoặc màu phù hợp
                    .border(2.dp, MaterialTheme.colorScheme.background, CircleShape) // Viền trắng
                    .padding(8.dp)
                    .clickable { /* Xử lý thay đổi ảnh đại diện */ }
            )
        }

        // Input Fields (có padding riêng)
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Name",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            EditableInputField(
                value = name,
                onValueChange = { name = it },
                placeholder = "Full Name",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Email",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            EditableInputField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Email Address",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Password",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            EditableInputField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                modifier = Modifier.fillMaxWidth(),
                isPassword = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Date of Birth (Dropdown Placeholder)
            Text(
                text = "Date of Birth",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            DropdownPlaceholder(
                value = dateOfBirth,
                placeholder = "Select Date",
                modifier = Modifier.fillMaxWidth(),
                onClick = { /* Mở Date Picker */ }
            )
            Spacer(modifier = Modifier.height(32.dp)) // Đã bỏ Spacer sau Country/Region

            // Country/Region (Dropdown Placeholder) - Đã bỏ
            // Text(text = "Country/Region", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(bottom = 4.dp))
            // DropdownPlaceholder(
            //     value = countryRegion,
            //     placeholder = "Select Country",
            //     modifier = Modifier.fillMaxWidth(),
            //     onClick = { /* Mở Country Selector */ }
            // )
            // Spacer(modifier = Modifier.height(32.dp)) // Đã bỏ

            // Save Button
            Button(
                onClick = {
                    // Xử lý logic lưu thông tin ở đây (ví dụ: gửi dữ liệu lên server)
                    // Sau khi lưu, quay lại màn hình Settings
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)), // Màu tím đậm
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Save changes",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF0F0F0)), // Màu nền của TextField
        colors = TextFieldDefaults.colors( // Changed from textFieldColors
            focusedContainerColor = Color(0xFFF0F0F0), // Use focusedContainerColor for M3
            unfocusedContainerColor = Color(0xFFF0F0F0), // Use unfocusedContainerColor for M3
            disabledContainerColor = Color(0xFFF0F0F0), // Use disabledContainerColor for M3
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp // Cỡ chữ nhỏ hơn một chút cho các trường nhập liệu
        ),
        placeholder = { Text(text = placeholder, color = Color.Gray) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownPlaceholder(
    value: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit // Callback khi click vào placeholder
) {
    TextField(
        value = value,
        onValueChange = { /* Không cho phép gõ trực tiếp */ },
        readOnly = true, // Quan trọng để ngăn bàn phím bật lên
        trailingIcon = { // Thêm icon mũi tên xuống
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Dropdown Arrow",
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF0F0F0)) // Màu nền của TextField
            .clickable { onClick() }, // Cho phép click để mở dropdown/picker
        colors = TextFieldDefaults.colors( // Changed from textFieldColors
            focusedContainerColor = Color(0xFFF0F0F0), // Use focusedContainerColor for M3
            unfocusedContainerColor = Color(0xFFF0F0F0), // Use unfocusedContainerColor for M3
            disabledContainerColor = Color(0xFFF0F0F0), // Use disabledContainerColor for M3
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp
        ),
        placeholder = { Text(text = placeholder, color = Color.Gray) }
    )
}


@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    Synkr1562Theme(darkTheme = false) { // Assuming Synkr1562Theme is your M3 theme
        EditProfileScreen(onNavigateBack = {})
    }
}
