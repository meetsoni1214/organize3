package com.example.organize3.appUi

import com.example.organize3.data.application.ApplicationAccount

data class ApplicationUiState (
    val id: Int = 0,
    val title: String = "",
    val username: String = "",
    val password: String = "",
    val remarks: String = "",
    val isArchived: Int = 0,
    val actionEnabled: Boolean = false
        )

// Extension function to convert [ApplicationUiState] to [ApplicationAccount].
fun ApplicationUiState.toApplicationAccount(): ApplicationAccount = ApplicationAccount(
    id = id,
    accountTitle = title,
    accountUsername = username,
    accountRemarks = remarks,
    accountPassword = password,
    isArchived = isArchived
)

// Extension function to convert [ApplicationAccount] to [ApplicationUiState]
fun ApplicationAccount.toApplicationUiState(actionEnabled: Boolean = false): ApplicationUiState = ApplicationUiState(
    id = id,
    title = accountTitle,
    username = accountUsername,
    password = accountPassword,
    remarks = accountRemarks,
    isArchived = isArchived
)

fun ApplicationUiState.isValid(): Boolean {
    return title.isNotBlank() && username.isNotBlank() && password.isNotBlank()
}