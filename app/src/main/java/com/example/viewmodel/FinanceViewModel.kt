package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AssistantMessage
import com.example.data.ai.FinancialInsight
import com.example.data.ai.GeminiAiService
import com.example.data.ai.ParsedExpense
import com.example.data.ai.ScannedReceipt
import com.example.data.local.AppDatabase
import com.example.data.local.BudgetEntity
import com.example.data.local.GoalEntity
import com.example.data.local.SubscriptionEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserPreferenceEntity
import com.example.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FinanceRepository(AppDatabase.getDatabase(application))

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBudgets: StateFlow<List<BudgetEntity>> = repository.allBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGoals: StateFlow<List<GoalEntity>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubscriptions: StateFlow<List<SubscriptionEntity>> = repository.allSubscriptions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userPreferences: StateFlow<UserPreferenceEntity?> = repository.userPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Search and Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedCategoryFilter = _selectedCategoryFilter.asStateFlow()

    private val _selectedTypeFilter = MutableStateFlow("ALL") // "ALL", "EXPENSE", "INCOME"
    val selectedTypeFilter = _selectedTypeFilter.asStateFlow()

    private val _selectedPaymentFilter = MutableStateFlow<String?>(null)
    val selectedPaymentFilter = _selectedPaymentFilter.asStateFlow()

    private val _selectedSort = MutableStateFlow(TransactionSort.DATE_NEWEST)
    val selectedSort = _selectedSort.asStateFlow()

    // Analytics Timeframe State
    private val _analyticsTimeframe = MutableStateFlow(AnalyticsTimeframe.DAYS_30)
    val analyticsTimeframe = _analyticsTimeframe.asStateFlow()

    data class FilterCriteria(
        val query: String = "",
        val category: String? = null,
        val type: String = "ALL",
        val payment: String? = null,
        val sort: TransactionSort = TransactionSort.DATE_NEWEST
    )

    private val filterCriteria = combine(
        combine(_searchQuery, _selectedCategoryFilter, _selectedTypeFilter) { q, cat, type ->
            Triple(q, cat, type)
        },
        combine(_selectedPaymentFilter, _selectedSort) { pay, sort ->
            Pair(pay, sort)
        }
    ) { (q, cat, type), (pay, sort) ->
        FilterCriteria(q, cat, type, pay, sort)
    }

    // Filtered Transactions Flow
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        filterCriteria
    ) { list, criteria ->
        var result = list

        if (criteria.query.isNotBlank()) {
            val q = criteria.query.trim().lowercase()
            result = result.filter {
                it.merchant.lowercase().contains(q) ||
                        it.category.lowercase().contains(q) ||
                        it.note.lowercase().contains(q) ||
                        it.amount.toString().contains(q)
            }
        }

        if (!criteria.category.isNullOrBlank() && criteria.category != "All") {
            result = result.filter { it.category.equals(criteria.category, ignoreCase = true) }
        }

        if (criteria.type != "ALL") {
            result = result.filter { it.type.equals(criteria.type, ignoreCase = true) }
        }

        if (!criteria.payment.isNullOrBlank() && criteria.payment != "All") {
            result = result.filter { it.paymentMethod.equals(criteria.payment, ignoreCase = true) }
        }

        when (criteria.sort) {
            TransactionSort.DATE_NEWEST -> result.sortedByDescending { it.dateMillis }
            TransactionSort.DATE_OLDEST -> result.sortedBy { it.dateMillis }
            TransactionSort.AMOUNT_HIGHEST -> result.sortedByDescending { it.amount }
            TransactionSort.AMOUNT_LOWEST -> result.sortedBy { it.amount }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Assistant Chat State
    private val _chatMessages = MutableStateFlow<List<AssistantMessage>>(
        listOf(
            AssistantMessage(
                isUser = false,
                text = "Hello! I am Penny AI, your personal financial assistant. I have full context on your recorded transactions, monthly budgets, and goals.\n\nAsk me anything like:\n• \"Where did I spend the most this month?\"\n• \"How much did I spend on food?\"\n• \"Can I afford a ₹5,000 purchase?\"\n• \"Give me a summary of my finances.\""
            )
        )
    )
    val chatMessages = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking = _isAiThinking.asStateFlow()

    // Smart Expense NLP State
    private val _smartParsedExpense = MutableStateFlow<ParsedExpense?>(null)
    val smartParsedExpense = _smartParsedExpense.asStateFlow()

    private val _isSmartParsing = MutableStateFlow(false)
    val isSmartParsing = _isSmartParsing.asStateFlow()

    // Receipt Scan State
    private val _scannedReceipt = MutableStateFlow<ScannedReceipt?>(null)
    val scannedReceipt = _scannedReceipt.asStateFlow()

    private val _isReceiptScanning = MutableStateFlow(false)
    val isReceiptScanning = _isReceiptScanning.asStateFlow()

    init {
        viewModelScope.launch {
            repository.checkAndLoadInitialData()
        }
    }

    // --- Search & Filter Actions ---
    fun setSearchQuery(q: String) { _searchQuery.value = q }
    fun setCategoryFilter(c: String?) { _selectedCategoryFilter.value = c }
    fun setTypeFilter(t: String) { _selectedTypeFilter.value = t }
    fun setPaymentFilter(p: String?) { _selectedPaymentFilter.value = p }
    fun setSort(s: TransactionSort) { _selectedSort.value = s }
    fun setAnalyticsTimeframe(tf: AnalyticsTimeframe) { _analyticsTimeframe.value = tf }

    // --- Transaction CRUD ---
    fun addTransaction(
        type: String,
        amount: Double,
        category: String,
        merchant: String,
        note: String,
        dateMillis: Long,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            repository.insertTransaction(
                TransactionEntity(
                    type = type,
                    amount = amount,
                    category = category,
                    merchant = merchant.ifBlank { if (type == "INCOME") "Income" else "Expense" },
                    note = note,
                    dateMillis = dateMillis,
                    paymentMethod = paymentMethod
                )
            )
        }
    }

    fun updateTransaction(tx: TransactionEntity) {
        viewModelScope.launch {
            repository.updateTransaction(tx)
        }
    }

    fun deleteTransaction(tx: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(tx)
        }
    }

    // --- Budget CRUD ---
    fun addBudget(name: String, amount: Double, category: String) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val monthYear = String.format("%04d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
            repository.insertBudget(
                BudgetEntity(
                    name = name,
                    amount = amount,
                    category = category,
                    monthYear = monthYear
                )
            )
        }
    }

    fun updateBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.updateBudget(budget)
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
        }
    }

    // --- Goal CRUD ---
    fun addGoal(name: String, targetAmount: Double, initialAmount: Double = 0.0, category: String = "General") {
        viewModelScope.launch {
            repository.insertGoal(
                GoalEntity(
                    name = name,
                    targetAmount = targetAmount,
                    currentAmount = initialAmount,
                    category = category
                )
            )
        }
    }

    fun contributeToGoal(goal: GoalEntity, contributionAmount: Double) {
        viewModelScope.launch {
            repository.updateGoal(goal.copy(currentAmount = (goal.currentAmount + contributionAmount).coerceAtMost(goal.targetAmount * 1.5)))
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    // --- Subscription CRUD ---
    fun addSubscription(
        name: String,
        amount: Double,
        billingCycle: String,
        category: String,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            cal.add(Calendar.MONTH, 1)
            repository.insertSubscription(
                SubscriptionEntity(
                    name = name,
                    amount = amount,
                    billingCycle = billingCycle,
                    nextPaymentDateMillis = cal.timeInMillis,
                    category = category,
                    paymentMethod = paymentMethod
                )
            )
        }
    }

    fun deleteSubscription(subscription: SubscriptionEntity) {
        viewModelScope.launch {
            repository.deleteSubscription(subscription)
        }
    }

    // --- Settings & Reset ---
    fun updateCurrency(code: String, symbol: String) {
        viewModelScope.launch {
            repository.updateCurrency(code, symbol)
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            repository.updateUserName(name)
        }
    }

    fun resetToSampleData() {
        viewModelScope.launch {
            repository.resetToSampleData()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    // --- AI Smart Expense Extraction ---
    fun parseNaturalLanguageExpense(text: String) {
        if (text.isBlank()) return
        _isSmartParsing.value = true
        _smartParsedExpense.value = null
        viewModelScope.launch {
            val symbol = userPreferences.value?.currencySymbol ?: "₹"
            val parsed = GeminiAiService.parseExpenseInput(text, symbol)
            _smartParsedExpense.value = parsed
            _isSmartParsing.value = false
        }
    }

    fun clearSmartParsedExpense() {
        _smartParsedExpense.value = null
    }

    fun confirmParsedExpense(parsed: ParsedExpense) {
        addTransaction(
            type = parsed.type,
            amount = parsed.amount,
            category = parsed.category,
            merchant = parsed.merchant,
            note = parsed.note,
            dateMillis = parsed.dateMillis,
            paymentMethod = parsed.paymentMethod
        )
        _smartParsedExpense.value = null
    }

    // --- AI Receipt Scanner ---
    fun processReceipt(bitmap: Bitmap?) {
        _isReceiptScanning.value = true
        _scannedReceipt.value = null
        viewModelScope.launch {
            val symbol = userPreferences.value?.currencySymbol ?: "₹"
            val receipt = GeminiAiService.scanReceipt(bitmap, symbol)
            _scannedReceipt.value = receipt
            _isReceiptScanning.value = false
        }
    }

    fun clearScannedReceipt() {
        _scannedReceipt.value = null
    }

    fun confirmScannedReceipt(receipt: ScannedReceipt) {
        addTransaction(
            type = "EXPENSE",
            amount = receipt.totalAmount,
            category = receipt.category,
            merchant = receipt.merchant,
            note = receipt.items.joinToString(", "),
            dateMillis = receipt.dateMillis,
            paymentMethod = receipt.paymentMethod
        )
        _scannedReceipt.value = null
    }

    // --- AI Assistant Chat ---
    fun sendChatMessage(question: String) {
        if (question.isBlank()) return
        val userMsg = AssistantMessage(isUser = true, text = question)
        _chatMessages.value = _chatMessages.value + userMsg
        _isAiThinking.value = true

        viewModelScope.launch {
            val symbol = userPreferences.value?.currencySymbol ?: "₹"
            val response = GeminiAiService.askAssistant(
                question = question,
                currencySymbol = symbol,
                transactions = allTransactions.value,
                budgets = allBudgets.value,
                goals = allGoals.value
            )
            _chatMessages.value = _chatMessages.value + response
            _isAiThinking.value = false
        }
    }

    fun getInsights(): List<FinancialInsight> {
        val symbol = userPreferences.value?.currencySymbol ?: "₹"
        return GeminiAiService.generateInsights(symbol, allTransactions.value, allBudgets.value)
    }
}
