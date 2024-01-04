@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.organize3.applications.details

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
import androidx.compose.material3.MaterialTheme
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
import com.example.organize3.emailAccounts.details.OtherDetails
import com.example.organize3.emailAccounts.details.TitleRow
import kotlinx.coroutines.launch


@Composable
fun ApplicationDetailScreen(
    modifier: Modifier = Modifier,
    navigateBack:() -> Unit,
    uiState: ApplicationUiState,
    gotoEditScreen:(String) -> Unit,
    onDeleteConfirmed: () -> Unit,
    onArchive: () -> Unit,
    onDuplicate: () -> Unit,
) {
    Scaffold(
        topBar = {
            OrganizeTopAppBar(
                title = stringResource(id = R.string.application_account),
                canNavigateBack = true,
                showMenu = true,
                isArchived = (uiState.isArchived == 1),
                navigateUp = navigateBack,
                shareSubject = stringResource(id = R.string.application_account),
                shareText = stringResource(id = R.string.share_application_detail,
                uiState.title,
                uiState.username,
                uiState.password,
                uiState.remarks),
                unArchive = {},
                deleteEmail = onDeleteConfirmed,
                archive = onArchive,
                duplicateEmail = onDuplicate
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { gotoEditScreen(uiState.selectedApplicationId!!)},
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = stringResource(id = R.string.edit_app_account_details))
            }
        }
    ) { innerPadding ->
        ApplicationAccountDetails(
            modifier = modifier.padding(innerPadding),
            applicationUiState = uiState
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
