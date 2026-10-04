package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class ChartBarData(val label: String, val value: Float)
data class CategorySlice(val category: String, val amount: Double, val color: Color)

@Composable
fun CategoryDonutChart(
    slices: List<CategorySlice>,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val total = slices.sumOf { it.amount }
    if (total <= 0.0) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No spending recorded in this period",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
        return
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Donut Canvas (Pure Black & White)
        Box(
            modifier = Modifier.size(150.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(130.dp)) {
                var startAngle = -90f
                val strokeWidth = 20.dp.toPx()

                // Draw outer track ring
                drawCircle(
                    color = Color.Black,
                    style = Stroke(width = strokeWidth)
                )

                // White separator gaps between slices
                slices.forEach { slice ->
                    val sweepAngle = ((slice.amount / total) * 360f).toFloat()
                    drawArc(
                        color = Color.White,
                        startAngle = startAngle - 2f,
                        sweepAngle = 4f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth + 2f)
                    )
                    startAngle += sweepAngle
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black
                )
                Text(
                    text = CurrencyFormatter.format(total, currencySymbol),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.width(20.dp))

        // Legend (Strictly Black & White)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            slices.take(5).forEach { slice ->
                val pct = ((slice.amount / total) * 100).toInt()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = slice.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = "$pct%",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleBarChart(
    bars: List<ChartBarData>,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    if (bars.isEmpty()) return
    val maxValue = bars.maxOfOrNull { it.value }?.coerceAtLeast(100f) ?: 100f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            bars.forEach { bar ->
                val ratio = (bar.value / maxValue).coerceIn(0.04f, 1f)
                val animatedRatio by animateFloatAsState(targetValue = ratio, animationSpec = tween(500), label = "bar")

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .fillMaxHeight(animatedRatio)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(Color.Black)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = bar.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun IncomeVsExpenseComparisonBar(
    income: Double,
    expense: Double,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val total = (income + expense).coerceAtLeast(1.0)
    val incomeRatio = (income / total).toFloat()
    val expenseRatio = (expense / total).toFloat()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Income: ${CurrencyFormatter.format(income, currencySymbol)}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Text(
                text = "Spent: ${CurrencyFormatter.format(expense, currencySymbol)}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dual Segment Bar in Black and White with border
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(CircleShape)
                .border(1.dp, Color.Black, CircleShape)
        ) {
            if (incomeRatio > 0f) {
                Box(
                    modifier = Modifier
                        .weight(incomeRatio)
                        .fillMaxHeight()
                        .background(Color.Black)
                )
            }
            if (expenseRatio > 0f) {
                Box(
                    modifier = Modifier
                        .weight(expenseRatio)
                        .fillMaxHeight()
                        .background(Color.White)
                )
            }
        }
    }
}
