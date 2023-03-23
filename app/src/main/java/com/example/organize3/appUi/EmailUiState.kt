package com.example.organize3.appUi

import com.example.organize3.data.email.EmailAccount

data class EmailUiState (
    val id: Int = 0,
    val title: String = "",
    val email: String = "",
    val password: String = "",
    val remarks: String = "",
    val actionEnabled: Boolean = false
)
// Extension function to convert [EmailUiState] to [EmailAccount].
fun EmailUiState.toEmailAccount(): EmailAccount = EmailAccount(
    id = id,
    accountTitle = title,
    accountEmail = email,
    accountPassword = password,
    accountRemarks = remarks
)

// Extension function to convert [EmailAccount] to [EmailUiState]
fun EmailAccount.toEmailUiState(actionEnabled: Boolean = false): EmailUiState = EmailUiState(
    id = id,
    title = accountTitle,
    email = accountEmail,
    password = accountPassword,
    remarks = accountRemarks
)

fun EmailUiState.isValid(): Boolean {
    return title.isNotBlank() && email.isNotBlank() && password.isNotBlank()
}
