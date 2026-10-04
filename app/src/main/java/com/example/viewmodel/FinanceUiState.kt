package com.example.viewmodel

enum class TransactionSort {
    DATE_NEWEST,
    DATE_OLDEST,
    AMOUNT_HIGHEST,
    AMOUNT_LOWEST
}

enum class AnalyticsTimeframe(val label: String, val days: Int) {
    DAYS_7("7 Days", 7),
    DAYS_30("30 Days", 30),
    MONTHS_3("3 Months", 90),
    MONTHS_6("6 Months", 180),
    YEAR_1("1 Year", 365)
}

data class CategorySpending(
    val category: String,
    val amount: Double,
    val percentage: Float
)
