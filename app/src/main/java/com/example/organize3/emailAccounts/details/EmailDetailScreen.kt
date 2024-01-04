
package com.example.organize3.emailAccounts.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.R
import com.example.organize3.emailAccounts.add_update.EmailUiState


@Composable
fun EmailDetailScreen(
    navigateBack:() -> Unit,
    modifier: Modifier = Modifier,
    uiState: EmailUiState,
    goToEditScreen:(String) -> Unit,
    onDeleteConfirmed: () -> Unit,
    onArchive: () -> Unit,
    onDuplicate: () -> Unit
) {
    Scaffold (
        topBar = {
                OrganizeTopAppBar(
                    title = stringResource(id = R.string.email_account_details),
                    canNavigateBack = true,
                    navigateUp = navigateBack,
                    showMenu = true,
                    isArchived = (uiState.isArchived == 1),
                    shareSubject = stringResource(id = R.string.email_account_details),
                    shareText = stringResource(
                        R.string.share_email_detail,
                        uiState.title,
                        uiState.email,
                        uiState.password,
                        uiState.remarks
                    ),
                    unArchive = {},
                    deleteEmail = onDeleteConfirmed,
                    archive = onArchive,
                    duplicateEmail = onDuplicate,
                )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { goToEditScreen(uiState.selectedEmailId!!) },
                shape = MaterialTheme.shapes.medium,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = stringResource(id = R.string.edit_email_account))
            }
        }
            ){ innerPadding ->
        EmailAccountDetails(
            emailUiState = uiState,
            modifier = modifier.padding(innerPadding)
        )
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
                    .size(100.dp),
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