package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.TransactionEntity
import com.example.ui.components.*
import com.example.viewmodel.AnalyticsTimeframe
import java.util.Calendar

@Composable
fun AnalyticsScreen(
    currencySymbol: String,
    transactions: List<TransactionEntity>,
    selectedTimeframe: AnalyticsTimeframe,
    onTimeframeChange: (AnalyticsTimeframe) -> Unit,
    modifier: Modifier = Modifier
) {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, -selectedTimeframe.days)
    val cutoffMillis = cal.timeInMillis

    val filtered = transactions.filter { it.dateMillis >= cutoffMillis }
    val expenseTx = filtered.filter { it.type == "EXPENSE" }
    val incomeTx = filtered.filter { it.type == "INCOME" }

    val totalExpense = expenseTx.sumOf { it.amount }
    val totalIncome = incomeTx.sumOf { it.amount }

    // Slices for Category Donut
    val categoryTotals = expenseTx.groupBy { it.category }
        .mapValues { it.value.sumOf { tx -> tx.amount } }
        .toList()
        .sortedByDescending { it.second }

    val donutSlices = categoryTotals.map { (cat, amount) ->
        CategorySlice(
            category = cat,
            amount = amount,
            color = Color.Black
        )
    }

    // Monthly / Trend Bar Data
    val barsData = when (selectedTimeframe) {
        AnalyticsTimeframe.DAYS_7 -> {
            (6 downTo 0).map { dayOffset ->
                val c = Calendar.getInstance()
                c.add(Calendar.DAY_OF_YEAR, -dayOffset)
                val dayStart = c.apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0) }.timeInMillis
                val dayEnd = c.apply { set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59) }.timeInMillis
                val sum = expenseTx.filter { it.dateMillis in dayStart..dayEnd }.sumOf { it.amount }.toFloat()
                ChartBarData(
                    label = when (dayOffset) {
                        0 -> "Today"
                        1 -> "Yest"
                        else -> java.text.SimpleDateFormat("E", java.util.Locale.getDefault()).format(c.time)
                    },
                    value = sum
                )
            }
        }
        AnalyticsTimeframe.DAYS_30 -> {
            (3 downTo 0).map { weekOffset ->
                val end = System.currentTimeMillis() - weekOffset * 7 * 86400000L
                val start = end - 7 * 86400000L
                val sum = expenseTx.filter { it.dateMillis in start..end }.sumOf { it.amount }.toFloat()
                ChartBarData(label = "Wk ${4 - weekOffset}", value = sum)
            }
        }
        else -> {
            (3 downTo 0).map { monthOffset ->
                val c = Calendar.getInstance()
                c.add(Calendar.MONTH, -monthOffset)
                val monthLabel = java.text.SimpleDateFormat("MMM", java.util.Locale.getDefault()).format(c.time)
                val sum = expenseTx.filter {
                    val txCal = Calendar.getInstance().apply { timeInMillis = it.dateMillis }
                    txCal.get(Calendar.MONTH) == c.get(Calendar.MONTH) && txCal.get(Calendar.YEAR) == c.get(Calendar.YEAR)
                }.sumOf { it.amount }.toFloat()
                ChartBarData(label = monthLabel, value = sum)
            }
        }
    }

    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = Color.Black,
        selectedLabelColor = Color.White,
        containerColor = Color.White,
        labelColor = Color.Black
    )
    val chipBorder = FilterChipDefaults.filterChipBorder(
        enabled = true,
        selected = false,
        borderColor = Color.Black,
        selectedBorderColor = Color.Black
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("analytics_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Spending Analytics",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                Text(
                    text = "Visual breakdown of expenses and trends",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black
                )
            }
        }

        // Timeframe Selector Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnalyticsTimeframe.values().forEach { tf ->
                    FilterChip(
                        selected = selectedTimeframe == tf,
                        onClick = { onTimeframeChange(tf) },
                        label = { Text(tf.label) },
                        colors = chipColors,
                        border = chipBorder
                    )
                }
            }
        }

        // 1. Income vs Expense Comparison Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Cash Flow",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.Black
                    )
                    IncomeVsExpenseComparisonBar(
                        income = totalIncome,
                        expense = totalExpense,
                        currencySymbol = currencySymbol
                    )
                }
            }
        }

        // 2. Category Breakdown Donut Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Category Breakdown",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.Black
                    )
                    CategoryDonutChart(
                        slices = donutSlices,
                        currencySymbol = currencySymbol
                    )
                }
            }
        }

        // 3. Spending Trend Bar Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Spending Trend",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SimpleBarChart(
                        bars = barsData,
                        currencySymbol = currencySymbol
                    )
                }
            }
        }

        // 4. Key Financial Health Numbers
        item {
            val dailyAvg = if (selectedTimeframe.days > 0) totalExpense / selectedTimeframe.days else 0.0
            val savingsRate = if (totalIncome > 0) ((totalIncome - totalExpense) / totalIncome) * 100 else 0.0

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Key Metrics",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Daily Average", style = MaterialTheme.typography.labelSmall, color = Color.Black)
                            Text(CurrencyFormatter.format(dailyAvg, currencySymbol), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                        }
                        Column {
                            Text("Savings Rate", style = MaterialTheme.typography.labelSmall, color = Color.Black)
                            Text("${String.format("%.1f", savingsRate.coerceAtLeast(0.0))}%", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                        }
                        Column {
                            Text("Top Category", style = MaterialTheme.typography.labelSmall, color = Color.Black)
                            Text(categoryTotals.firstOrNull()?.first ?: "N/A", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
