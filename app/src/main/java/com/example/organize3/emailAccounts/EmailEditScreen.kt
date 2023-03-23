@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.organize3.emailAccounts

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
fun EmailEditScreen(
    modifier: Modifier = Modifier,
    onNavigateUp:() -> Unit,
    navigateBack:() -> Unit,
    viewModel: EmailEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold (
        topBar = {
            OrganizeTopAppBar(
                title = stringResource(id = R.string.edit_email_account),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
            ){ innerPadding ->
                EmailEntryBody(
                    emailUiState = viewModel.emailUiState,
                    onEmailValueChange = viewModel::updateUiState,
                    onSaveClick = {
                        coroutineScope.launch {
                            viewModel.updateEmail()
                            navigateBack()
                        }
                    },
                    modifier = modifier.padding(innerPadding)
                )
    }
}