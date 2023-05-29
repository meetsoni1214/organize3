
package com.example.organize3.emailAccounts

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.organize3.AppViewModelProvider
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.R
import com.example.organize3.appUi.EmailUiState
import com.example.organize3.appUi.toEmailAccount
import kotlinx.coroutines.launch

@Composable
fun EmailDetailScreen(
    navigateBack:() -> Unit,
    modifier: Modifier = Modifier,
    goToEditScreen:(Int) -> Unit,
    viewModel: EmailAccountDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val deletedText = stringResource(id = R.string.email_account_deleted)
    var showSnackbar by remember { mutableStateOf(false) }
    val uiState = viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val isArchived = viewModel.isArchived
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    androidx.compose.material.Scaffold (
        scaffoldState = scaffoldState,
        topBar = {
                OrganizeTopAppBar(
                    title = stringResource(id = R.string.email_account_details),
                    canNavigateBack = true,
                    navigateUp = navigateBack,
                    showMenu = true,
                    isArchived = (isArchived == 1),
                    shareSubject = stringResource(id = R.string.email_account_details),
                    shareText = stringResource(
                        R.string.share_email_detail,
                        uiState.value.title,
                        uiState.value.email,
                        uiState.value.password,
                        uiState.value.remarks
                    ),
                    unArchive = {
                                coroutineScope.launch {
                                    viewModel.unArchiveEmail()
                                    navigateBack()
                                }
                    },
                    deleteEmail = {
                            coroutineScope.launch {
                                viewModel.deleteEmail(uiState.value.toEmailAccount())
                                navigateBack()
                            }
                    },
                    archive = {
                        coroutineScope.launch {
                            viewModel.archiveEmail()
                            navigateBack()
                            showSnackbar = true
                        }
                    },
                    duplicateEmail = {
                        coroutineScope.launch {
                            viewModel.duplicateEmail()
                            navigateBack()
                        }
                    }
                )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { goToEditScreen(uiState.value.id) },
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = stringResource(id = R.string.edit_email_account))
            }
        }
            ){ innerPadding ->
        EmailAccountDetails(
            emailUiState = uiState.value,
            modifier = modifier.padding(innerPadding)
        )

        if (showSnackbar) {
            LaunchedEffect(scaffoldState, deletedText) {
                scaffoldState.snackbarHostState.showSnackbar(deletedText)
                showSnackbar = false
            }
        }
    }
}

@Composable
fun EmailAccountDetails(
    emailUiState: EmailUiState,
    modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())) {
            TitleRow(text = emailUiState.title, icon = R.drawable.email_icon)
            OtherDetails(value = emailUiState.email, title = R.string.email_id)
            OtherDetails(value = emailUiState.password, title = R.string.password)
            OtherDetails(value = emailUiState.remarks, title = R.string.remarks)
    }
}

@Composable
fun TitleRow(
    text: String,
    icon: Int,
    modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SelectionContainer {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .width(180.dp)
            )
        }
            Image(
                painter = painterResource(id = icon),
                contentDescription = stringResource(id = R.string.email_icon),
                modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
    }
}

@Composable
fun OtherDetails(
    modifier: Modifier = Modifier,
    value: String,
    title: Int
) {
    SelectionContainer {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = value,
                modifier = Modifier.padding(start = 8.dp),
                fontSize = 24.sp
            )
        }
    }

}

//@Preview(showBackground = true)
//@Composable
//fun TitleRowPreview() {
//    TitleRow()
//}
//
//@Preview(showBackground = true)
//@Composable
//fun OtherDetailsPreview() {
//    OtherDetails()
//}

//@Preview(showBackground = true)
//@Composable
//fun FullScreenPreview() {
//    EmailDetailScreen()
//}