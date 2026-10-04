package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.ai.FinancialInsight
import com.example.data.local.BudgetEntity
import com.example.data.local.TransactionEntity
import com.example.ui.components.CurrencyFormatter
import com.example.ui.components.DashboardOverviewCard
import com.example.ui.components.TransactionItemRow

@Composable
fun DashboardScreen(
    userName: String,
    currencySymbol: String,
    transactions: List<TransactionEntity>,
    budgets: List<BudgetEntity>,
    insights: List<FinancialInsight>,
    onAddExpenseClick: () -> Unit,
    onSmartAiClick: () -> Unit,
    onScanReceiptClick: () -> Unit,
    onAskAiClick: () -> Unit,
    onViewAllTransactions: () -> Unit,
    onManageBudgetsClick: () -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
    val totalSpent = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val netBalance = totalIncome - totalSpent

    val overallBudget = budgets.find { it.category == "ALL" }?.amount ?: 30000.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Top Greeting Header ("Good morning, [Name]")
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = CurrencyFormatter.getGreeting(userName),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    Text(
                        text = "Here is your financial status today",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                }
                // AI Assistant Quick Access Icon (Black & White)
                IconButton(
                    onClick = onAskAiClick,
                    modifier = Modifier
                        .border(1.dp, Color.Black, CircleShape)
                        .testTag("dashboard_ask_ai_icon_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Ask Finance AI",
                        tint = Color.Black
                    )
                }
            }
        }

        // 2. Main Top Financial Overview Card (Balance, Income, Spent, Remaining, and Progress Indicator)
        item {
            DashboardOverviewCard(
                balance = netBalance,
                income = totalIncome,
                spent = totalSpent,
                budgetAmount = overallBudget,
                currencySymbol = currencySymbol,
                onManageBudgetsClick = onManageBudgetsClick
            )
        }

        // 3. Quick Action Buttons Row ("+ Add Expense", "Smart AI Entry", "Scan Receipt")
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Primary "+ Add Expense" (Solid black button, white text)
                Button(
                    onClick = onAddExpenseClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("dashboard_add_expense_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Record", fontWeight = FontWeight.SemiBold)
                }

                // AI Smart Natural Language Entry (White button, black border)
                OutlinedButton(
                    onClick = onSmartAiClick,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.Black),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("dashboard_smart_ai_btn")
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Smart AI Entry")
                }

                // Receipt Scanner (White button, black border)
                OutlinedButton(
                    onClick = onScanReceiptClick,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.Black),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("dashboard_scan_receipt_btn")
                ) {
                    Icon(imageVector = Icons.Default.DocumentScanner, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Scan Receipt")
                }
            }
        }

        // 4. AI Financial Insights Section
        if (insights.isNotEmpty()) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI Financial Insights",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        insights.forEach { insight ->
                            InsightCard(insight = insight, onAskAiClick = onAskAiClick)
                        }
                    }
                }
            }
        }

        // 5. Recent Transactions Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                TextButton(
                    onClick = onViewAllTransactions,
                    modifier = Modifier.testTag("dashboard_view_all_tx"),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                ) {
                    Text("View All")
                }
            }
        }

        val recentTx = transactions.take(6)
        if (recentTx.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No transactions yet",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add your first expense to start understanding your spending.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onAddExpenseClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+ Add Expense")
                        }
                    }
                }
            }
        } else {
            items(recentTx, key = { it.id }) { tx ->
                TransactionItemRow(
                    transaction = tx,
                    currencySymbol = currencySymbol,
                    onClick = { onTransactionClick(tx) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun InsightCard(insight: FinancialInsight, onAskAiClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(148.dp)
            .clickable { onAskAiClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = insight.title,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = insight.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black,
                    maxLines = 3
                )
            }

            if (insight.calculationDetail != null) {
                Text(
                    text = "• ${insight.calculationDetail}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black,
                    maxLines = 1
                )
            }
        }
    }
}
