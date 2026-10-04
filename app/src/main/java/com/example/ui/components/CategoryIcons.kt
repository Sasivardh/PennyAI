package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.*

object CategoryHelper {
    val allCategories = listOf(
        "Food",
        "Transport",
        "Shopping",
        "Bills",
        "Entertainment",
        "Education",
        "Health",
        "Travel",
        "Subscriptions",
        "Salary",
        "Other"
    )

    val allPaymentMethods = listOf(
        "UPI",
        "Cash",
        "Debit Card",
        "Credit Card",
        "Bank Transfer",
        "Other"
    )

    fun getIcon(category: String): ImageVector {
        return when (category.lowercase()) {
            "food" -> Icons.Default.Restaurant
            "transport" -> Icons.Default.DirectionsCar
            "shopping" -> Icons.Default.ShoppingBag
            "bills" -> Icons.Default.Receipt
            "entertainment" -> Icons.Default.Movie
            "education" -> Icons.Default.School
            "health" -> Icons.Default.MedicalServices
            "travel" -> Icons.Default.Flight
            "subscriptions" -> Icons.Default.Repeat
            "salary", "income" -> Icons.Default.AccountBalanceWallet
            else -> Icons.Default.Category
        }
    }

    fun getColor(category: String): Color {
        return Color.Black
    }

    fun getPaymentIcon(method: String): ImageVector {
        return when (method.lowercase()) {
            "upi" -> Icons.Default.Smartphone
            "cash" -> Icons.Default.Payments
            "debit card", "credit card" -> Icons.Default.CreditCard
            "bank transfer" -> Icons.Default.AccountBalance
            else -> Icons.Default.Payment
        }
    }
}
