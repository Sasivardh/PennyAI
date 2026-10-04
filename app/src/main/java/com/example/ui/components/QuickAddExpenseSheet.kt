package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.TransactionEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddExpenseSheet(
    currencySymbol: String,
    existingTransaction: TransactionEntity? = null,
    onDismiss: () -> Unit,
    onSave: (
        type: String,
        amount: Double,
        category: String,
        merchant: String,
        note: String,
        paymentMethod: String
    ) -> Unit,
    onDelete: ((TransactionEntity) -> Unit)? = null
) {
    var type by remember { mutableStateOf(existingTransaction?.type ?: "EXPENSE") }
    var amountText by remember { mutableStateOf(existingTransaction?.amount?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "") }
    var category by remember { mutableStateOf(existingTransaction?.category ?: "Food") }
    var merchantText by remember { mutableStateOf(existingTransaction?.merchant ?: "") }
    var noteText by remember { mutableStateOf(existingTransaction?.note ?: "") }
    var paymentMethod by remember { mutableStateOf(existingTransaction?.paymentMethod ?: "UPI") }

    var customCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryInput by remember { mutableStateOf("") }
    var categoriesList by remember { mutableStateOf(CategoryHelper.allCategories) }

    var amountError by remember { mutableStateOf(false) }

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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Black) },
        modifier = Modifier.testTag("add_expense_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (existingTransaction == null) "New Record" else "Edit Record",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Income / Expense Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = type == "EXPENSE",
                    onClick = { type = "EXPENSE" },
                    label = { Text("Expense", fontWeight = FontWeight.Medium) },
                    colors = chipColors,
                    border = chipBorder,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("type_expense_chip")
                )
                FilterChip(
                    selected = type == "INCOME",
                    onClick = {
                        type = "INCOME"
                        if (category == "Food") category = "Salary"
                    },
                    label = { Text("Income", fontWeight = FontWeight.Medium) },
                    colors = chipColors,
                    border = chipBorder,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("type_income_chip")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Amount Input
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it
                    amountError = false
                },
                label = { Text("Amount", color = Color.Black) },
                prefix = { Text("$currencySymbol ", fontWeight = FontWeight.Bold, color = Color.Black) },
                isError = amountError,
                supportingText = if (amountError) { { Text("Please enter a valid amount", color = Color.Black) } } else null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expense_amount_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Merchant / Title
            OutlinedTextField(
                value = merchantText,
                onValueChange = { merchantText = it },
                label = { Text(if (type == "INCOME") "Source / Payer" else "Merchant / Place", color = Color.Black) },
                placeholder = { Text(if (type == "INCOME") "e.g. Salary, Client" else "e.g. KFC, Uber, Grocery", color = Color.Black) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expense_merchant_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Category Selection
            Text(
                text = "Category",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categoriesList.forEach { cat ->
                    FilterChip(
                        selected = category.equals(cat, ignoreCase = true),
                        onClick = { category = cat },
                        label = { Text(cat) },
                        leadingIcon = {
                            Icon(
                                imageVector = CategoryHelper.getIcon(cat),
                                contentDescription = null,
                                tint = if (category.equals(cat, ignoreCase = true)) Color.White else Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = chipColors,
                        border = chipBorder
                    )
                }
                AssistChip(
                    onClick = { customCategoryDialog = true },
                    label = { Text("+ Custom", color = Color.Black) },
                    border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = Color.Black)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Payment Method Selection
            Text(
                text = "Payment Method",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryHelper.allPaymentMethods.forEach { method ->
                    FilterChip(
                        selected = paymentMethod == method,
                        onClick = { paymentMethod = method },
                        label = { Text(method) },
                        leadingIcon = {
                            Icon(
                                imageVector = CategoryHelper.getPaymentIcon(method),
                                contentDescription = null,
                                tint = if (paymentMethod == method) Color.White else Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = chipColors,
                        border = chipBorder
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Note (Optional)
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Note (Optional)", color = Color.Black) },
                placeholder = { Text("Add context, tags or details", color = Color.Black) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { /* hide keyboard */ }),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expense_note_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button (Solid Black with White Text)
            Button(
                onClick = {
                    val amountVal = amountText.toDoubleOrNull()
                    if (amountVal == null || amountVal <= 0.0) {
                        amountError = true
                    } else {
                        onSave(
                            type,
                            amountVal,
                            category,
                            merchantText.trim(),
                            noteText.trim(),
                            paymentMethod
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_expense_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (existingTransaction == null) "Save Transaction" else "Update Transaction",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            if (existingTransaction != null && onDelete != null) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        onDelete(existingTransaction)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("delete_expense_button"),
                    border = BorderStroke(1.dp, Color.Black),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Record")
                }
            }
        }
    }

    if (customCategoryDialog) {
        AlertDialog(
            onDismissRequest = { customCategoryDialog = false },
            title = { Text("Add Custom Category", color = Color.Black) },
            text = {
                OutlinedTextField(
                    value = newCategoryInput,
                    onValueChange = { newCategoryInput = it },
                    label = { Text("Category Name", color = Color.Black) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            containerColor = Color.White,
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newCategoryInput.isNotBlank()) {
                            val trimmed = newCategoryInput.trim()
                            categoriesList = categoriesList + trimmed
                            category = trimmed
                            newCategoryInput = ""
                            customCategoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { customCategoryDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
