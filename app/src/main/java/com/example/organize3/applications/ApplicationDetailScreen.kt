@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.organize3.applications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.organize3.AppViewModelProvider
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.R
import com.example.organize3.appUi.ApplicationUiState
import com.example.organize3.appUi.toApplicationAccount
import com.example.organize3.emailAccounts.OtherDetails
import com.example.organize3.emailAccounts.TitleRow
import kotlinx.coroutines.launch


@Composable
fun ApplicationDetailScreen(
    modifier: Modifier = Modifier,
    navigateBack:() -> Unit,
    gotoEditScreen:(Int) -> Unit,
    viewModel: ApplicationDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val isArchived = viewModel.isArchived
    Scaffold(
        topBar = {
            OrganizeTopAppBar(
                title = stringResource(id = R.string.application_account),
                canNavigateBack = true,
                showMenu = true,
                isArchived = (isArchived == 1),
                navigateUp = navigateBack,
                shareSubject = stringResource(id = R.string.application_account),
                shareText = stringResource(id = R.string.share_application_detail,
                uiState.value.title,
                uiState.value.username,
                uiState.value.password,
                uiState.value.remarks),
                unArchive = {
                            coroutineScope.launch {
                                viewModel.unArchiveApplication()
                                navigateBack()
                            }
                },
                deleteEmail = {
                    coroutineScope.launch {
                        viewModel.deleteApplication(uiState.value.toApplicationAccount())
                        navigateBack()
                    }
                },
                archive = {
                          coroutineScope.launch {
                              viewModel.archiveApplication()
                              navigateBack()
                          }
                },
                duplicateEmail = {
                    coroutineScope.launch {
                        viewModel.duplicateApplication()
                        navigateBack()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { gotoEditScreen(uiState.value.id)},
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = stringResource(id = R.string.edit_app_account_details))
            }
        }
    ) { innerPadding ->
        ApplicationAccountDetails(
            modifier = modifier.padding(innerPadding),
            applicationUiState = uiState.value
        )
    }
}

@Composable
fun ApplicationAccountDetails(
    modifier: Modifier = Modifier,
    applicationUiState: ApplicationUiState
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
    ) {
        TitleRow(text = applicationUiState.title, icon = R.drawable.website_logo)
        OtherDetails(value = applicationUiState.username, title = R.string.facebook_login_title)
        OtherDetails(value = applicationUiState.password, title = R.string.password)
        OtherDetails(value = applicationUiState.remarks, title = R.string.remarks)
    }
}

