@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)

package com.example.organize3.bankAccounts

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
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.organize3.AppViewModelProvider
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.R
import com.example.organize3.data.bankAccount.BankAccount
import kotlinx.coroutines.launch

@Composable
fun AddedBankAccountScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp:() -> Unit,
    onAddAccount: () -> Unit,
    goToBankAccountScreen: (Int, Int) -> Unit,
    viewModel: BankAccountHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val bankAccountHomeUiState by viewModel.bankAccountHomeUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddAccount() }) {
                Icon(imageVector = Icons.Default.Add,
                    contentDescription = null)
            }
        },
    topBar = {
        OrganizeTopAppBar(
            title = stringResource(id = R.string.bank_account),
            canNavigateBack = canNavigateBack,
            navigateUp = onNavigateUp
        )
    }) { values -> 
        BankAccountScreen(
            modifier.padding(values),
            goToBankAccountScreen = goToBankAccountScreen,
            bankAccountList = bankAccountHomeUiState.bankAccounts,
            deleteAccount = {bankAccount ->
                coroutineScope.launch {
                    viewModel.archiveBankAccount(bankAccount)
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
fun BankAccountScreen(
    modifier: Modifier = Modifier,
    goToBankAccountScreen: (Int, Int) -> Unit,
    deleteAccount: (BankAccount) -> Unit,
    bankAccountList: List<BankAccount>
) {
    if (bankAccountList.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.category_screen_text_bank),
                modifier = Modifier.padding(horizontal = 16.dp))
        }
    }else {
        Column(modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
            BankAccountList(
                bankAccountList = bankAccountList,
                goToBankAccountScreen = goToBankAccountScreen,
                deleteAccount = deleteAccount
            )
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BankAccountList(
    modifier: Modifier = Modifier,
    goToBankAccountScreen: (Int, Int) -> Unit,
    deleteAccount: (BankAccount) -> Unit,
    bankAccountList: List<BankAccount>
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items = bankAccountList, key = {it.id}) {bankAccount ->
            val dismissState = rememberDismissState()

            if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                deleteAccount(bankAccount)
            }

            SwipeToDismiss(
                state = dismissState,
                modifier = Modifier
                    .padding(vertical = Dp(1f)),
                directions = setOf(
                    DismissDirection.EndToStart
                ),
                dismissThresholds = {direction ->
                    androidx.compose.material.FractionalThreshold(if (direction == DismissDirection.EndToStart) 0.3f else 0.05f)
                },
                background = {
                    val color by animateColorAsState(
                        when (dismissState.targetValue) {
                            DismissValue.Default -> Color.White
                            else -> Color.Green
                        }
                    )
                    val alignment = Alignment.CenterEnd
                    val icon =  R.drawable.ic_archived

                    val scale by animateFloatAsState(
                        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color)
                            .padding(horizontal = Dp(20f))
                                ,
                        contentAlignment = alignment
                    ) {
                        Image(
                            painter = painterResource(id = icon),
                            contentDescription = "Archive Icon",
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
                        BankAccountCard(
                            bankAccount = bankAccount,
                            goToBankAccountScreen = goToBankAccountScreen
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun BankAccountCard(
    modifier: Modifier = Modifier,
    goToBankAccountScreen: (Int, Int) -> Unit,
    bankAccount: BankAccount
) {
    Card(modifier = modifier
        .fillMaxWidth()
        .clickable { goToBankAccountScreen(bankAccount.id, 0) }
        .padding(4.dp)) {
        Row(
            modifier = Modifier
                .padding(end = 6.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = bankAccount.bankLogo),
                contentDescription = stringResource(
                id = R.string.bank_logo),
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = bankAccount.bankName,
                    modifier = Modifier
                        .padding(start = 8.dp, top = 12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = bankAccount.accountHolderName,
                    modifier = Modifier
                        .padding(start = 8.dp, bottom = 12.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewBankAccountCard() {
//    BankAccountCard(bankAccount = BankAccount())
//}