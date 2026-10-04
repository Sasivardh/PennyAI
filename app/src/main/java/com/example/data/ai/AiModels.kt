package com.example.data.ai

data class ParsedExpense(
    val amount: Double,
    val category: String,
    val merchant: String,
    val dateMillis: Long,
    val paymentMethod: String,
    val type: String = "EXPENSE",
    val note: String = "",
    val confidence: String = "High"
)

data class ScannedReceipt(
    val merchant: String,
    val totalAmount: Double,
    val dateMillis: Long,
    val category: String,
    val items: List<String> = emptyList(),
    val paymentMethod: String = "UPI",
    val taxes: Double = 0.0
)

data class FinancialInsight(
    val id: String,
    val type: InsightType,
    val title: String,
    val message: String,
    val calculationDetail: String? = null,
    val actionText: String? = null
)

enum class InsightType {
    PATTERN,
    CATEGORY_ALERT,
    SAVING_OPPORTUNITY,
    UNUSUAL_SPENDING,
    POSITIVE_FEEDBACK
}

data class AssistantMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val calculation: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
