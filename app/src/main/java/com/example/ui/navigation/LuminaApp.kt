package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.TransactionEntity
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuminaApp(viewModel: FinanceViewModel) {
    var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }

    // Dialog & Sheet States
    var showQuickAddSheet by remember { mutableStateOf(false) }
    var transactionToEdit by remember { mutableStateOf<TransactionEntity?>(null) }
    var showSmartExpenseDialog by remember { mutableStateOf(false) }
    var showReceiptScanDialog by remember { mutableStateOf(false) }
    var showAskAiSheet by remember { mutableStateOf(false) }

    // Observables
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val budgets by viewModel.allBudgets.collectAsStateWithLifecycle()
    val goals by viewModel.allGoals.collectAsStateWithLifecycle()
    val subscriptions by viewModel.allSubscriptions.collectAsStateWithLifecycle()
    val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategoryFilter by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
    val selectedTypeFilter by viewModel.selectedTypeFilter.collectAsStateWithLifecycle()
    val selectedPaymentFilter by viewModel.selectedPaymentFilter.collectAsStateWithLifecycle()
    val selectedSort by viewModel.selectedSort.collectAsStateWithLifecycle()
    val analyticsTimeframe by viewModel.analyticsTimeframe.collectAsStateWithLifecycle()

    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isAiThinking by viewModel.isAiThinking.collectAsStateWithLifecycle()

    val smartParsedExpense by viewModel.smartParsedExpense.collectAsStateWithLifecycle()
    val isSmartParsing by viewModel.isSmartParsing.collectAsStateWithLifecycle()

    val scannedReceipt by viewModel.scannedReceipt.collectAsStateWithLifecycle()
    val isReceiptScanning by viewModel.isReceiptScanning.collectAsStateWithLifecycle()

    val currencySymbol = userPreferences?.currencySymbol ?: "₹"
    val userName = userPreferences?.userName ?: "Alex"

    // Back button behavior: if on a secondary screen, navigate back to Dashboard
    if (currentScreen != Screen.DASHBOARD) {
        BackHandler {
            currentScreen = Screen.DASHBOARD
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val isWideScreen = maxWidth >= 600.dp

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Adaptive Navigation Rail for Expanded/Tablets
            if (isWideScreen) {
                NavigationRail(
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    modifier = Modifier.testTag("nav_rail")
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    FloatingActionButton(
                        onClick = { showQuickAddSheet = true },
                        shape = CircleShape,
                        containerColor = Color.Black,
                        contentColor = Color.White,
                        modifier = Modifier.testTag("rail_fab_add")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    val railItemColors = NavigationRailItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.Black,
                        unselectedIconColor = Color.Black,
                        unselectedTextColor = Color.Black,
                        indicatorColor = Color.Black
                    )

                    Screen.values().forEach { screen ->
                        NavigationRailItem(
                            selected = currentScreen == screen,
                            onClick = {
                                if (screen == Screen.AI_ASSISTANT) {
                                    showAskAiSheet = true
                                } else {
                                    currentScreen = screen
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (currentScreen == screen) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) },
                            colors = railItemColors,
                            modifier = Modifier.testTag("nav_rail_${screen.name.lowercase()}")
                        )
                    }
                }
                VerticalDivider(color = Color.Black, thickness = 1.dp)
            }

            // Main Screen Scaffold
            Scaffold(
                containerColor = Color.White,
                contentColor = Color.Black,
                topBar = {
                    Column {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (currentScreen == Screen.DASHBOARD) "Penny AI" else currentScreen.title,
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color.Black
                                    )
                                }
                            },
                            actions = {
                                if (!isWideScreen) {
                                    if (currentScreen != Screen.BUDGETS) {
                                        IconButton(
                                            onClick = { currentScreen = Screen.BUDGETS },
                                            modifier = Modifier.testTag("top_bar_budgets_btn")
                                        ) {
                                            Icon(Icons.Default.PieChart, contentDescription = "Budgets", tint = Color.Black)
                                        }
                                    }
                                    if (currentScreen != Screen.GOALS) {
                                        IconButton(
                                            onClick = { currentScreen = Screen.GOALS },
                                            modifier = Modifier.testTag("top_bar_goals_btn")
                                        ) {
                                            Icon(Icons.Default.Savings, contentDescription = "Goals", tint = Color.Black)
                                        }
                                    }
                                    if (currentScreen != Screen.SUBSCRIPTIONS) {
                                        IconButton(
                                            onClick = { currentScreen = Screen.SUBSCRIPTIONS },
                                            modifier = Modifier.testTag("top_bar_subscriptions_btn")
                                        ) {
                                            Icon(Icons.Default.Repeat, contentDescription = "Subscriptions", tint = Color.Black)
                                        }
                                    }
                                }
                                IconButton(
                                    onClick = { currentScreen = Screen.SETTINGS },
                                    modifier = Modifier.testTag("top_bar_settings_btn")
                                ) {
                                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.Black)
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.White,
                                titleContentColor = Color.Black,
                                actionIconContentColor = Color.Black,
                                navigationIconContentColor = Color.Black
                            )
                        )
                        HorizontalDivider(color = Color.Black, thickness = 1.dp)
                    }
                },
                bottomBar = {
                    if (!isWideScreen) {
                        val navItemColors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.Black,
                            unselectedIconColor = Color.Black,
                            unselectedTextColor = Color.Black,
                            indicatorColor = Color.Black
                        )
                        Column {
                            HorizontalDivider(color = Color.Black, thickness = 1.dp)
                            NavigationBar(
                                containerColor = Color.White,
                                contentColor = Color.Black,
                                tonalElevation = 0.dp,
                                modifier = Modifier.testTag("bottom_nav_bar")
                            ) {
                                NavigationBarItem(
                                    selected = currentScreen == Screen.DASHBOARD,
                                    onClick = { currentScreen = Screen.DASHBOARD },
                                    icon = {
                                        Icon(
                                            imageVector = if (currentScreen == Screen.DASHBOARD) Screen.DASHBOARD.selectedIcon else Screen.DASHBOARD.unselectedIcon,
                                            contentDescription = "Home"
                                        )
                                    },
                                    label = { Text("Home") },
                                    colors = navItemColors,
                                    modifier = Modifier.testTag("bottom_nav_home")
                                )

                                NavigationBarItem(
                                    selected = currentScreen == Screen.TRANSACTIONS,
                                    onClick = { currentScreen = Screen.TRANSACTIONS },
                                    icon = {
                                        Icon(
                                            imageVector = if (currentScreen == Screen.TRANSACTIONS) Screen.TRANSACTIONS.selectedIcon else Screen.TRANSACTIONS.unselectedIcon,
                                            contentDescription = "Transactions"
                                        )
                                    },
                                    label = { Text("History") },
                                    colors = navItemColors,
                                    modifier = Modifier.testTag("bottom_nav_transactions")
                                )

                                // Center Prominent Add Button
                                NavigationBarItem(
                                    selected = false,
                                    onClick = { showQuickAddSheet = true },
                                    icon = {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(Color.Black, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Add Expense",
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    },
                                    label = { Text("Add") },
                                    colors = navItemColors,
                                    modifier = Modifier.testTag("bottom_nav_add")
                                )

                                NavigationBarItem(
                                    selected = currentScreen == Screen.ANALYTICS,
                                    onClick = { currentScreen = Screen.ANALYTICS },
                                    icon = {
                                        Icon(
                                            imageVector = if (currentScreen == Screen.ANALYTICS) Screen.ANALYTICS.selectedIcon else Screen.ANALYTICS.unselectedIcon,
                                            contentDescription = "Analytics"
                                        )
                                    },
                                    label = { Text("Analytics") },
                                    colors = navItemColors,
                                    modifier = Modifier.testTag("bottom_nav_analytics")
                                )

                                NavigationBarItem(
                                    selected = false,
                                    onClick = { showAskAiSheet = true },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Filled.AutoAwesome,
                                            contentDescription = "Finance AI",
                                            tint = Color.Black
                                        )
                                    },
                                    label = { Text("AI") },
                                    colors = navItemColors,
                                    modifier = Modifier.testTag("bottom_nav_ai")
                                )
                            }
                        }
                    }
                },
                contentWindowInsets = WindowInsets.systemBars
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(Color.White)
                ) {
                    when (currentScreen) {
                        Screen.DASHBOARD -> DashboardScreen(
                            userName = userName,
                            currencySymbol = currencySymbol,
                            transactions = transactions,
                            budgets = budgets,
                            insights = viewModel.getInsights(),
                            onAddExpenseClick = { showQuickAddSheet = true },
                            onSmartAiClick = { showSmartExpenseDialog = true },
                            onScanReceiptClick = { showReceiptScanDialog = true },
                            onAskAiClick = { showAskAiSheet = true },
                            onViewAllTransactions = { currentScreen = Screen.TRANSACTIONS },
                            onManageBudgetsClick = { currentScreen = Screen.BUDGETS },
                            onTransactionClick = { tx ->
                                transactionToEdit = tx
                                showQuickAddSheet = true
                            }
                        )

                        Screen.TRANSACTIONS -> TransactionsScreen(
                            currencySymbol = currencySymbol,
                            transactions = filteredTransactions,
                            searchQuery = searchQuery,
                            selectedCategory = selectedCategoryFilter,
                            selectedType = selectedTypeFilter,
                            selectedPayment = selectedPaymentFilter,
                            selectedSort = selectedSort,
                            onSearchChange = viewModel::setSearchQuery,
                            onCategoryChange = viewModel::setCategoryFilter,
                            onTypeChange = viewModel::setTypeFilter,
                            onPaymentChange = viewModel::setPaymentFilter,
                            onSortChange = viewModel::setSort,
                            onAddClick = { showQuickAddSheet = true },
                            onTransactionClick = { tx ->
                                transactionToEdit = tx
                                showQuickAddSheet = true
                            }
                        )

                        Screen.BUDGETS -> BudgetsScreen(
                            currencySymbol = currencySymbol,
                            budgets = budgets,
                            transactions = transactions,
                            onAddBudget = viewModel::addBudget,
                            onDeleteBudget = viewModel::deleteBudget
                        )

                        Screen.ANALYTICS -> AnalyticsScreen(
                            currencySymbol = currencySymbol,
                            transactions = transactions,
                            selectedTimeframe = analyticsTimeframe,
                            onTimeframeChange = viewModel::setAnalyticsTimeframe
                        )

                        Screen.GOALS -> GoalsScreen(
                            currencySymbol = currencySymbol,
                            goals = goals,
                            onAddGoal = viewModel::addGoal,
                            onContribute = viewModel::contributeToGoal,
                            onDeleteGoal = viewModel::deleteGoal
                        )

                        Screen.SUBSCRIPTIONS -> SubscriptionsScreen(
                            currencySymbol = currencySymbol,
                            subscriptions = subscriptions,
                            onAddSubscription = viewModel::addSubscription,
                            onDeleteSubscription = viewModel::deleteSubscription
                        )

                        Screen.AI_ASSISTANT -> {
                            LaunchedEffect(Unit) {
                                showAskAiSheet = true
                            }
                            DashboardScreen(
                                userName = userName,
                                currencySymbol = currencySymbol,
                                transactions = transactions,
                                budgets = budgets,
                                insights = viewModel.getInsights(),
                                onAddExpenseClick = { showQuickAddSheet = true },
                                onSmartAiClick = { showSmartExpenseDialog = true },
                                onScanReceiptClick = { showReceiptScanDialog = true },
                                onAskAiClick = { showAskAiSheet = true },
                                onViewAllTransactions = { currentScreen = Screen.TRANSACTIONS },
                                onManageBudgetsClick = { currentScreen = Screen.BUDGETS },
                                onTransactionClick = { tx ->
                                    transactionToEdit = tx
                                    showQuickAddSheet = true
                                }
                            )
                        }

                        Screen.SETTINGS -> SettingsScreen(
                            userPreferences = userPreferences,
                            onCurrencyChange = viewModel::updateCurrency,
                            onUserNameChange = viewModel::updateUserName,
                            onResetSampleData = viewModel::resetToSampleData,
                            onClearAllData = viewModel::clearAllData
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet: Quick Add / Edit Expense
    if (showQuickAddSheet) {
        QuickAddExpenseSheet(
            currencySymbol = currencySymbol,
            existingTransaction = transactionToEdit,
            onDismiss = {
                showQuickAddSheet = false
                transactionToEdit = null
            },
            onSave = { type, amount, category, merchant, note, paymentMethod ->
                if (transactionToEdit != null) {
                    viewModel.updateTransaction(
                        transactionToEdit!!.copy(
                            type = type,
                            amount = amount,
                            category = category,
                            merchant = merchant,
                            note = note,
                            paymentMethod = paymentMethod
                        )
                    )
                } else {
                    viewModel.addTransaction(
                        type = type,
                        amount = amount,
                        category = category,
                        merchant = merchant,
                        note = note,
                        dateMillis = System.currentTimeMillis(),
                        paymentMethod = paymentMethod
                    )
                }
                showQuickAddSheet = false
                transactionToEdit = null
            },
            onDelete = { tx ->
                viewModel.deleteTransaction(tx)
                showQuickAddSheet = false
                transactionToEdit = null
            }
        )
    }

    // Dialog: Smart Natural Language AI Expense
    if (showSmartExpenseDialog) {
        SmartExpenseDialog(
            currencySymbol = currencySymbol,
            isParsing = isSmartParsing,
            parsedExpense = smartParsedExpense,
            onDismiss = {
                showSmartExpenseDialog = false
                viewModel.clearSmartParsedExpense()
            },
            onSubmitPrompt = viewModel::parseNaturalLanguageExpense,
            onConfirm = { parsed ->
                viewModel.confirmParsedExpense(parsed)
                showSmartExpenseDialog = false
            },
            onEditFallback = { parsed ->
                viewModel.clearSmartParsedExpense()
                showSmartExpenseDialog = false
                transactionToEdit = TransactionEntity(
                    type = parsed.type,
                    amount = parsed.amount,
                    category = parsed.category,
                    merchant = parsed.merchant,
                    note = parsed.note,
                    dateMillis = parsed.dateMillis,
                    paymentMethod = parsed.paymentMethod
                )
                showQuickAddSheet = true
            }
        )
    }

    // Dialog: AI Receipt Scanner
    if (showReceiptScanDialog) {
        ReceiptScanDialog(
            currencySymbol = currencySymbol,
            isScanning = isReceiptScanning,
            scannedReceipt = scannedReceipt,
            onDismiss = {
                showReceiptScanDialog = false
                viewModel.clearScannedReceipt()
            },
            onProcessReceipt = viewModel::processReceipt,
            onConfirm = { receipt ->
                viewModel.confirmScannedReceipt(receipt)
                showReceiptScanDialog = false
            },
            onEditFallback = { receipt ->
                viewModel.clearScannedReceipt()
                showReceiptScanDialog = false
                transactionToEdit = TransactionEntity(
                    type = "EXPENSE",
                    amount = receipt.totalAmount,
                    category = receipt.category,
                    merchant = receipt.merchant,
                    note = receipt.items.joinToString(", "),
                    dateMillis = receipt.dateMillis,
                    paymentMethod = receipt.paymentMethod
                )
                showQuickAddSheet = true
            }
        )
    }

    // Sheet: Ask Finance AI Assistant
    if (showAskAiSheet) {
        AskAiAssistantSheet(
            messages = chatMessages,
            isThinking = isAiThinking,
            onSendMessage = viewModel::sendChatMessage,
            onDismiss = {
                showAskAiSheet = false
                if (currentScreen == Screen.AI_ASSISTANT) {
                    currentScreen = Screen.DASHBOARD
                }
            }
        )
    }
}
