@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.organize3.bankAccounts

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.organize3.AppViewModelProvider
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.R
import kotlinx.coroutines.launch

@Composable
fun BankAccountEditScreen(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
    navigateBack: () -> Unit,
//    goToEditBankScreen: () -> Unit,
    viewModel: BankAccountEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
        val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            OrganizeTopAppBar(
                title = stringResource(id = R.string.edit_app_account_details),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }) {innerPadding ->
        AddOtherDetailsBody(
            modifier = modifier.padding(innerPadding),
            bankAccountUiState = viewModel.bankAccountUiState,
            onValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.updateBankAccount()
                    navigateBack()
                }
            },
//            goToAddBankScreen = goToEditBankScreen
        )
    }
}