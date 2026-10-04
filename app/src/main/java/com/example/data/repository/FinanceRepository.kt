package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class FinanceRepository(private val database: AppDatabase) {
    private val transactionDao = database.transactionDao()
    private val budgetDao = database.budgetDao()
    private val goalDao = database.goalDao()
    private val subscriptionDao = database.subscriptionDao()
    private val userPreferenceDao = database.userPreferenceDao()

    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allBudgets: Flow<List<BudgetEntity>> = budgetDao.getAllBudgets()
    val allGoals: Flow<List<GoalEntity>> = goalDao.getAllGoals()
    val allSubscriptions: Flow<List<SubscriptionEntity>> = subscriptionDao.getAllSubscriptions()
    val userPreferences: Flow<UserPreferenceEntity?> = userPreferenceDao.getUserPreferences()

    suspend fun checkAndLoadInitialData() {
        val prefs = userPreferenceDao.getUserPreferencesSync()
        if (prefs == null) {
            val defaultPref = UserPreferenceEntity(
                id = 1,
                userName = "Alex",
                currencyCode = "INR",
                currencySymbol = "₹",
                monthlyIncomeTarget = 55000.0,
                sampleDataLoaded = true
            )
            userPreferenceDao.insertOrUpdate(defaultPref)
            transactionDao.insertAll(SampleData.getInitialTransactions())
            budgetDao.insertAll(SampleData.getInitialBudgets())
            goalDao.insertAll(SampleData.getInitialGoals())
            subscriptionDao.insertAll(SampleData.getInitialSubscriptions())
        }
    }

    suspend fun resetToSampleData() {
        transactionDao.clearAll()
        budgetDao.clearAll()
        goalDao.clearAll()
        subscriptionDao.clearAll()

        transactionDao.insertAll(SampleData.getInitialTransactions())
        budgetDao.insertAll(SampleData.getInitialBudgets())
        goalDao.insertAll(SampleData.getInitialGoals())
        subscriptionDao.insertAll(SampleData.getInitialSubscriptions())

        val currentPref = userPreferenceDao.getUserPreferencesSync() ?: UserPreferenceEntity()
        userPreferenceDao.insertOrUpdate(currentPref.copy(sampleDataLoaded = true))
    }

    suspend fun clearAllData() {
        transactionDao.clearAll()
        budgetDao.clearAll()
        goalDao.clearAll()
        subscriptionDao.clearAll()
        val currentPref = userPreferenceDao.getUserPreferencesSync() ?: UserPreferenceEntity()
        userPreferenceDao.insertOrUpdate(currentPref.copy(sampleDataLoaded = false))
    }

    // Transactions CRUD
    suspend fun insertTransaction(transaction: TransactionEntity): Long = transactionDao.insertTransaction(transaction)
    suspend fun updateTransaction(transaction: TransactionEntity) = transactionDao.updateTransaction(transaction)
    suspend fun deleteTransaction(transaction: TransactionEntity) = transactionDao.deleteTransaction(transaction)
    suspend fun deleteTransactionById(id: Long) = transactionDao.deleteById(id)

    // Budgets CRUD
    suspend fun insertBudget(budget: BudgetEntity): Long = budgetDao.insertBudget(budget)
    suspend fun updateBudget(budget: BudgetEntity) = budgetDao.updateBudget(budget)
    suspend fun deleteBudget(budget: BudgetEntity) = budgetDao.deleteBudget(budget)
    suspend fun deleteBudgetById(id: Long) = budgetDao.deleteById(id)

    // Goals CRUD
    suspend fun insertGoal(goal: GoalEntity): Long = goalDao.insertGoal(goal)
    suspend fun updateGoal(goal: GoalEntity) = goalDao.updateGoal(goal)
    suspend fun deleteGoal(goal: GoalEntity) = goalDao.deleteGoal(goal)
    suspend fun deleteGoalById(id: Long) = goalDao.deleteById(id)

    // Subscriptions CRUD
    suspend fun insertSubscription(subscription: SubscriptionEntity): Long = subscriptionDao.insertSubscription(subscription)
    suspend fun updateSubscription(subscription: SubscriptionEntity) = subscriptionDao.updateSubscription(subscription)
    suspend fun deleteSubscription(subscription: SubscriptionEntity) = subscriptionDao.deleteSubscription(subscription)
    suspend fun deleteSubscriptionById(id: Long) = subscriptionDao.deleteById(id)

    // User Preferences
    suspend fun updatePreferences(preferences: UserPreferenceEntity) = userPreferenceDao.insertOrUpdate(preferences)
    suspend fun updateCurrency(code: String, symbol: String) {
        val current = userPreferenceDao.getUserPreferencesSync() ?: UserPreferenceEntity()
        userPreferenceDao.insertOrUpdate(current.copy(currencyCode = code, currencySymbol = symbol))
    }
    suspend fun updateUserName(name: String) {
        val current = userPreferenceDao.getUserPreferencesSync() ?: UserPreferenceEntity()
        userPreferenceDao.insertOrUpdate(current.copy(userName = name))
    }
    suspend fun updateDarkMode(isDark: Boolean?) {
        val current = userPreferenceDao.getUserPreferencesSync() ?: UserPreferenceEntity()
        userPreferenceDao.insertOrUpdate(current.copy(isDarkMode = isDark))
    }
}
