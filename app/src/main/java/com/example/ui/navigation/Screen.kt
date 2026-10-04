package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val showInBottomBar: Boolean = true
) {
    DASHBOARD("Home", Icons.Filled.Home, Icons.Outlined.Home, true),
    TRANSACTIONS("Transactions", Icons.AutoMirrored.Filled.ReceiptLong, Icons.AutoMirrored.Outlined.ReceiptLong, true),
    BUDGETS("Budgets", Icons.Filled.PieChart, Icons.Outlined.PieChart, false),
    ANALYTICS("Analytics", Icons.Filled.BarChart, Icons.Outlined.BarChart, true),
    GOALS("Goals", Icons.Filled.Savings, Icons.Outlined.Savings, false),
    SUBSCRIPTIONS("Subscriptions", Icons.Filled.Repeat, Icons.Outlined.Repeat, false),
    AI_ASSISTANT("Finance AI", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, true),
    SETTINGS("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, false)
}
