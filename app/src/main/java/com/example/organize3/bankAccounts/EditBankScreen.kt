@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)

package com.example.organize3.bankAccounts

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.R

@Composable
fun EditBankScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp:() -> Unit,
    gotoEditOtherDetailsScreen:(String, Int) -> Unit,
    bankViewModel: BankViewModel = viewModel()
) {
    val bankUiState by bankViewModel.bankAccount.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    Scaffold(topBar = {
        OrganizeTopAppBar(
            title = stringResource(id = R.string.select_bank),
            canNavigateBack = canNavigateBack,
            navigateUp = onNavigateUp
        )
    }) { values ->
        Screen(
            modifier.padding(values),
            searchedBankName = bankUiState.bankName,
            onKeyboardDone = { keyboardController?.hide() },
            bankViewModel = bankViewModel,
            goToAddOtherDetailScreen = gotoEditOtherDetailsScreen,
            onBankSearchChanged = {search ->
                bankViewModel.updateBankSearch(search)
            }
        )
    }
}