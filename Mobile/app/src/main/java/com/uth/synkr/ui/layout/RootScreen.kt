package com.uth.synkr.ui.layout

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.uth.synkr.navigation.AppRoute
import kotlinx.coroutines.launch

@Composable
fun RootScreen(
    navController: NavController, currentRoute: String, content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf("Home") }

    ModalNavigationDrawer(
        drawerState = drawerState, drawerContent = {
            NavDrawer(
                selectedItem = selectedItem, onItemSelected = { label ->
                    selectedItem = label
                    coroutineScope.launch { drawerState.close() }
                    when (label) {
                        "Home" -> navController.navigate(AppRoute.HOME)
                        "Contacts" -> navController.navigate(AppRoute.CONTACTS)
                        "Profile" -> navController.navigate(AppRoute.PROFILE)
                    }
                })
        }) {
        MainLayout(
            title = when (currentRoute) {
                AppRoute.HOME -> "Home"
                AppRoute.CONTACTS -> "Contacts"
                AppRoute.PROFILE -> "Profile"
                else -> ""
            }, onMenuClick = { coroutineScope.launch { drawerState.open() } }) {
            content()
        }
    }
}
