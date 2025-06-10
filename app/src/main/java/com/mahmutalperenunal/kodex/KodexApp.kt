package com.mahmutalperenunal.kodex

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.mahmutalperenunal.kodex.ui.screens.GeneratorScreen
import com.mahmutalperenunal.kodex.ui.screens.HistoryScreen
import com.mahmutalperenunal.kodex.ui.screens.HomeScreen
import com.mahmutalperenunal.kodex.ui.screens.ScannerScreen
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mahmutalperenunal.kodex.ui.components.AdMobBanner

@Composable
fun KodexApp() {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem("home", stringResource(R.string.home), BottomNavIcon.Drawable(R.drawable.home)),
        BottomNavItem("scanner", stringResource(R.string.scan), BottomNavIcon.Drawable(R.drawable.qr_code_scanner)),
        BottomNavItem("generator", stringResource(R.string.generate), BottomNavIcon.Drawable(R.drawable.add)),
        BottomNavItem("history", stringResource(R.string.history), BottomNavIcon.Drawable(R.drawable.history))
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AdMobBanner(
                        adUnitId = "ca-app-pub-xxxxxxxxxxxxxxxx/zzzzzzzzzz",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    )
                    NavigationBar {
                        bottomNavItems.forEach { item ->
                            NavigationBarItem(
                                icon = {
                                    when (val icon = item.icon) {
                                        is BottomNavIcon.Vector -> Icon(imageVector = icon.icon, contentDescription = item.label)
                                        is BottomNavIcon.Drawable -> Icon(
                                            painter = painterResource(id = icon.resId),
                                            contentDescription = item.label
                                        )
                                    }
                                },
                                label = { Text(item.label) },
                                selected = currentRoute == item.route,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") { HomeScreen(navController) }
                    composable("scanner") { ScannerScreen(navController) }
                    composable("generator") { GeneratorScreen(navController) }
                    composable("history") { HistoryScreen(navController) }
                }
            }
        }
    }
}

sealed class BottomNavIcon {
    data class Vector(val icon: ImageVector) : BottomNavIcon()
    data class Drawable(val resId: Int) : BottomNavIcon()
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: BottomNavIcon
)