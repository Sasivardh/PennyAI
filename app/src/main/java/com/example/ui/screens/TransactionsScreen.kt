package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.TransactionEntity
import com.example.ui.components.CategoryHelper
import com.example.ui.components.TransactionItemRow
import com.example.viewmodel.TransactionSort

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    currencySymbol: String,
    transactions: List<TransactionEntity>,
    searchQuery: String,
    selectedCategory: String?,
    selectedType: String,
    selectedPayment: String?,
    selectedSort: TransactionSort,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String?) -> Unit,
    onTypeChange: (String) -> Unit,
    onPaymentChange: (String?) -> Unit,
    onSortChange: (TransactionSort) -> Unit,
    onAddClick: () -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var sortMenuOpen by remember { mutableStateOf(false) }

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("transactions_screen")
    ) {
        // Search & Filter Header
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            // Search Bar (Black & White)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search by merchant, note, amount...", color = Color.Black) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Black) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Black)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transactions_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Type Filters (All, Expense, Income) + Sort Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedType == "ALL",
                        onClick = { onTypeChange("ALL") },
                        label = { Text("All") },
                        colors = chipColors,
                        border = chipBorder
                    )
                    FilterChip(
                        selected = selectedType == "EXPENSE",
                        onClick = { onTypeChange("EXPENSE") },
                        label = { Text("Expenses") },
                        colors = chipColors,
                        border = chipBorder
                    )
                    FilterChip(
                        selected = selectedType == "INCOME",
                        onClick = { onTypeChange("INCOME") },
                        label = { Text("Income") },
                        colors = chipColors,
                        border = chipBorder
                    )
                }

                Box {
                    IconButton(onClick = { sortMenuOpen = true }) {
                        Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort", tint = Color.Black)
                    }
                    DropdownMenu(
                        expanded = sortMenuOpen,
                        onDismissRequest = { sortMenuOpen = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Date (Newest First)", color = Color.Black) },
                            onClick = {
                                onSortChange(TransactionSort.DATE_NEWEST)
                                sortMenuOpen = false
                            },
                            leadingIcon = {
                                if (selectedSort == TransactionSort.DATE_NEWEST) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Date (Oldest First)", color = Color.Black) },
                            onClick = {
                                onSortChange(TransactionSort.DATE_OLDEST)
                                sortMenuOpen = false
                            },
                            leadingIcon = {
                                if (selectedSort == TransactionSort.DATE_OLDEST) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Amount (High to Low)", color = Color.Black) },
                            onClick = {
                                onSortChange(TransactionSort.AMOUNT_HIGHEST)
                                sortMenuOpen = false
                            },
                            leadingIcon = {
                                if (selectedSort == TransactionSort.AMOUNT_HIGHEST) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Amount (Low to High)", color = Color.Black) },
                            onClick = {
                                onSortChange(TransactionSort.AMOUNT_LOWEST)
                                sortMenuOpen = false
                            },
                            leadingIcon = {
                                if (selectedSort == TransactionSort.AMOUNT_LOWEST) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category Horizontal Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onCategoryChange(null) },
                    label = { Text("All Categories") },
                    colors = chipColors,
                    border = chipBorder
                )
                CategoryHelper.allCategories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { onCategoryChange(if (selectedCategory == cat) null else cat) },
                        label = { Text(cat) },
                        colors = chipColors,
                        border = chipBorder
                    )
                }
            }
        }

        HorizontalDivider(color = Color.Black, thickness = 1.dp)

        // List
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No transactions found",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try adjusting your filters or record a new expense.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onAddClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+ Add Expense")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions, key = { it.id }) { tx ->
                    TransactionItemRow(
                        transaction = tx,
                        currencySymbol = currencySymbol,
                        onClick = { onTransactionClick(tx) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}
