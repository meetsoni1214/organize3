@file:OptIn(ExperimentalMaterialApi::class)

package com.example.organize3.emailAccounts

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.SnackbarResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.organize3.AppViewModelProvider
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.R
import com.example.organize3.data.email.EmailAccount
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddedEmailAccountsScreen(
    navigateToEmailAccount: (Int) -> Unit,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp:() -> Unit,
    onAddEmail:() -> Unit,
    viewModel: EmailHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val emailHomeUiState by viewModel.emailHomeUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    Scaffold (
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEmail) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        topBar = {
            OrganizeTopAppBar(
                title = stringResource(id = R.string.email_category),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }){ values ->
        EmailScreen(
            modifier = modifier.padding(values),
            emailList = emailHomeUiState.emailList,
            onEmailClick = navigateToEmailAccount,
            deleteEmail = { emailAccount ->
                coroutineScope.launch {
                    viewModel.deleteEmail(emailAccount)

                }
                coroutineScope.launch {
                    val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                        message = "Item deleted!",
                        actionLabel = "Undo"
                    )
                    when (snackbarResult) {
                        SnackbarResult.Dismissed -> Log.d("SnackbarDemo", "Dismissed")
                        SnackbarResult.ActionPerformed -> {
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun EmailScreen(
    modifier: Modifier = Modifier,
    emailList: List<EmailAccount>,
    onEmailClick: (Int) -> Unit,
    deleteEmail: (EmailAccount) -> Unit
) {
    if (emailList.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(id = R.string.category_screen_text_email),
                modifier = Modifier.padding(horizontal = 16.dp))
        }
    }else {
        Column(modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            EmailList(onEmailClick = {onEmailClick(it.id)}, emailList = emailList, deleteEmail = deleteEmail)
        }
    }

}

@Composable
fun EmailList(
    modifier: Modifier = Modifier,
    onEmailClick: (EmailAccount) -> Unit,
    emailList: List<EmailAccount>,
    deleteEmail: (EmailAccount) -> Unit
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items = emailList, key = { it.id}) { email ->
            val dismissState = rememberDismissState()

            if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                deleteEmail(email)
            }

            SwipeToDismiss(
                state = dismissState,
                modifier = Modifier
                    .padding(vertical = Dp(1f)),
                directions = setOf(
                    DismissDirection.EndToStart
                ),
                dismissThresholds = { direction ->
                    androidx.compose.material.FractionalThreshold(if (direction == DismissDirection.EndToStart) 0.3f else 0.05f)
                },
                background = {
                    val color by animateColorAsState(
                        when (dismissState.targetValue) {
                            DismissValue.Default -> Color.White
                            else -> Color.Red
                        }
                    )
                    val alignment = Alignment.CenterEnd
                    val icon = Icons.Default.Delete

                    val scale by animateFloatAsState(
                        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
                    )
                    Box (
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color)
                            .padding(horizontal = Dp(20f))
                        ,
                        contentAlignment = alignment
                    ){
                        Icon(
                            imageVector = icon,
                            contentDescription = "Delete Icon",
                            modifier = Modifier.scale(scale)
                        )
                    }
                },
                dismissContent = {
                    Card(
                        elevation = animateDpAsState(
                            if (dismissState.dismissDirection != null) 4.dp else 0.dp
                        ).value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(alignment = Alignment.CenterVertically)
                    ) {
                        EmailAccountCard(email = email, onEmailClick = onEmailClick)
                    }
                }
            )
        }
    }
}

@Composable
fun EmailAccountCard(
    email: EmailAccount,
    onEmailClick: (EmailAccount) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier
        .padding(4.dp)
        .clickable { onEmailClick(email) }
        .fillMaxWidth()) {
        Row(modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.email_icon),
                contentDescription = stringResource(id = R.string.email_icon),
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
                    .padding(8.dp),
                contentScale = ContentScale.Crop,
            )
            Text(
                text = email.accountTitle,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmailCardPreview() {
    EmailAccountCard(
        email = EmailAccount(
            accountEmail = "meetsoni1214@gmail.com",
            accountTitle = "Meet Soni Gmail",
            accountRemarks = "jkda",
            accountPassword = "jkfa"
        ), onEmailClick = {}
    )
}