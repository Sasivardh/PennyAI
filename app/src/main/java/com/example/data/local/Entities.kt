package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "EXPENSE" or "INCOME"
    val amount: Double,
    val category: String, // "Food", "Transport", "Shopping", "Bills", "Entertainment", "Education", "Health", "Travel", "Subscriptions", "Other"
    val merchant: String,
    val note: String = "",
    val dateMillis: Long,
    val paymentMethod: String, // "UPI", "Cash", "Debit Card", "Credit Card", "Bank Transfer", "Other"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val amount: Double,
    val category: String, // "ALL" for overall monthly, or specific category name
    val period: String = "MONTHLY",
    val monthYear: String // "YYYY-MM", e.g. "2026-10"
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val targetDateMillis: Long = 0,
    val category: String = "General",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val amount: Double,
    val billingCycle: String = "MONTHLY", // "MONTHLY", "YEARLY"
    val nextPaymentDateMillis: Long,
    val category: String = "Subscriptions",
    val paymentMethod: String = "Credit Card",
    val active: Boolean = true
)

@Entity(tableName = "user_preferences")
data class UserPreferenceEntity(
    @PrimaryKey
    val id: Int = 1,
    val userName: String = "Alex",
    val currencyCode: String = "INR",
    val currencySymbol: String = "₹",
    val monthlyIncomeTarget: Double = 55000.0,
    val isDarkMode: Boolean? = null, // null = follow system
    val sampleDataLoaded: Boolean = false
)
