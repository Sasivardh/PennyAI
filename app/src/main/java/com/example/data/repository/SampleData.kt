package com.example.data.repository

import com.example.data.local.BudgetEntity
import com.example.data.local.GoalEntity
import com.example.data.local.SubscriptionEntity
import com.example.data.local.TransactionEntity
import java.util.Calendar

object SampleData {
    fun getInitialTransactions(): List<TransactionEntity> {
        val cal = Calendar.getInstance()
        val now = cal.timeInMillis

        fun daysAgo(days: Int, hour: Int = 12): Long {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -days)
            c.set(Calendar.HOUR_OF_DAY, hour)
            c.set(Calendar.MINUTE, 30)
            return c.timeInMillis
        }

        return listOf(
            TransactionEntity(
                type = "INCOME",
                amount = 55000.0,
                category = "Salary",
                merchant = "TechCorp Global",
                note = "Monthly salary deposit",
                dateMillis = daysAgo(3, 9),
                paymentMethod = "Bank Transfer"
            ),
            TransactionEntity(
                type = "EXPENSE",
                amount = 450.0,
                category = "Food",
                merchant = "Green Gourmet Bistro",
                note = "Dinner with team",
                dateMillis = now - 3600000 * 2, // 2 hours ago today
                paymentMethod = "UPI"
            ),
            TransactionEntity(
                type = "EXPENSE",
                amount = 320.0,
                category = "Transport",
                merchant = "Uber Premier",
                note = "Ride back from office",
                dateMillis = daysAgo(1, 19),
                paymentMethod = "UPI"
            ),
            TransactionEntity(
                type = "EXPENSE",
                amount = 2450.0,
                category = "Shopping",
                merchant = "Zara Lifestyle",
                note = "Autumn jacket & tee",
                dateMillis = daysAgo(1, 16),
                paymentMethod = "Credit Card"
            ),
            TransactionEntity(
                type = "EXPENSE",
                amount = 1800.0,
                category = "Shopping",
                merchant = "Amazon India",
                note = "Ergonomic keyboard wrist rest",
                dateMillis = daysAgo(2, 14),
                paymentMethod = "Credit Card"
            ),
            TransactionEntity(
                type = "EXPENSE",
                amount = 2650.0,
                category = "Bills",
                merchant = "City Power & Gas",
                note = "Electricity & utility bill",
                dateMillis = daysAgo(2, 10),
                paymentMethod = "Bank Transfer"
            ),
            TransactionEntity(
                type = "EXPENSE",
                amount = 1150.0,
                category = "Bills",
                merchant = "Airtel Fiber Broadband",
                note = "Gigabit home internet",
                dateMillis = daysAgo(3, 11),
                paymentMethod = "UPI"
            ),
            TransactionEntity(
                type = "EXPENSE",
                amount = 1450.0,
                category = "Entertainment",
                merchant = "PVR IMAX Cinemas",
                note = "Weekend sci-fi premiere",
                dateMillis = daysAgo(4, 20),
                paymentMethod = "Debit Card"
            ),
            TransactionEntity(
                type = "EXPENSE",
                amount = 1860.0,
                category = "Transport",
                merchant = "Shell Express",
                note = "Car petrol refill",
                dateMillis = daysAgo(4, 8),
                paymentMethod = "Credit Card"
            ),
            TransactionEntity(
                type = "EXPENSE",
                amount = 5970.0,
                category = "Food",
                merchant = "Nature's Basket Organic",
                note = "Fortnightly gourmet grocery haul",
                dateMillis = daysAgo(5, 17),
                paymentMethod = "Credit Card"
            ),
            TransactionEntity(
                type = "EXPENSE",
                amount = 649.0,
                category = "Subscriptions",
                merchant = "Netflix 4K Ultra",
                note = "Family stream subscription",
                dateMillis = daysAgo(6, 4),
                paymentMethod = "Credit Card"
            ),
            TransactionEntity(
                type = "EXPENSE",
                amount = 250.0,
                category = "Subscriptions",
                merchant = "Spotify Premium Duo",
                note = "High fidelity music",
                dateMillis = daysAgo(7, 3),
                paymentMethod = "UPI"
            )
        )
    }

    fun getInitialBudgets(): List<BudgetEntity> {
        val cal = Calendar.getInstance()
        val currentMonthYear = String.format("%04d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)

        return listOf(
            BudgetEntity(
                name = "Monthly Overall Budget",
                amount = 30000.0,
                category = "ALL",
                monthYear = currentMonthYear
            ),
            BudgetEntity(
                name = "Food & Groceries",
                amount = 10000.0,
                category = "Food",
                monthYear = currentMonthYear
            ),
            BudgetEntity(
                name = "Daily Transport",
                amount = 5000.0,
                category = "Transport",
                monthYear = currentMonthYear
            ),
            BudgetEntity(
                name = "Shopping & Apparel",
                amount = 6000.0,
                category = "Shopping",
                monthYear = currentMonthYear
            ),
            BudgetEntity(
                name = "Entertainment & Leisure",
                amount = 3000.0,
                category = "Entertainment",
                monthYear = currentMonthYear
            )
        )
    }

    fun getInitialGoals(): List<GoalEntity> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, 6)
        val targetDate1 = cal.timeInMillis
        cal.add(Calendar.MONTH, 4)
        val targetDate2 = cal.timeInMillis

        return listOf(
            GoalEntity(
                name = "New Laptop (MacBook M3)",
                targetAmount = 80000.0,
                currentAmount = 35000.0,
                targetDateMillis = targetDate1,
                category = "Electronics"
            ),
            GoalEntity(
                name = "6-Month Emergency Fund",
                targetAmount = 150000.0,
                currentAmount = 68000.0,
                targetDateMillis = targetDate2,
                category = "Safety"
            ),
            GoalEntity(
                name = "Japan Autumn Vacation",
                targetAmount = 120000.0,
                currentAmount = 42000.0,
                targetDateMillis = targetDate2,
                category = "Travel"
            )
        )
    }

    fun getInitialSubscriptions(): List<SubscriptionEntity> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 4)
        val nextDate1 = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, 8)
        val nextDate2 = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, 12)
        val nextDate3 = cal.timeInMillis

        return listOf(
            SubscriptionEntity(
                name = "Netflix 4K Premium",
                amount = 649.0,
                billingCycle = "MONTHLY",
                nextPaymentDateMillis = nextDate1,
                category = "Entertainment",
                paymentMethod = "Credit Card"
            ),
            SubscriptionEntity(
                name = "Spotify Premium Duo",
                amount = 250.0,
                billingCycle = "MONTHLY",
                nextPaymentDateMillis = nextDate2,
                category = "Music",
                paymentMethod = "UPI"
            ),
            SubscriptionEntity(
                name = "Google One 2TB Cloud",
                amount = 650.0,
                billingCycle = "MONTHLY",
                nextPaymentDateMillis = nextDate3,
                category = "Cloud Storage",
                paymentMethod = "Credit Card"
            ),
            SubscriptionEntity(
                name = "Amazon Prime Membership",
                amount = 1499.0,
                billingCycle = "YEARLY",
                nextPaymentDateMillis = nextDate3 + 86400000L * 45,
                category = "Shopping",
                paymentMethod = "Credit Card"
            )
        )
    }
}
