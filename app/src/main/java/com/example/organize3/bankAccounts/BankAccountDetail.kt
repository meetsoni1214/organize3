@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.organize3.bankAccounts

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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.organize3.AppViewModelProvider
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.R
import com.example.organize3.appUi.BankAccountUiState
import com.example.organize3.appUi.toBankAccount
import com.example.organize3.emailAccounts.OtherDetails
import com.example.organize3.emailAccounts.TitleRow
import kotlinx.coroutines.launch

@Composable
fun BankAccountDetailScreen(
    modifier: Modifier = Modifier,
    navigateBack:() -> Unit,
    goToEditScreen:(Int) -> Unit,
    viewModel: BankAccountDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState = viewModel.uiState.collectAsState()
    var isBankName by rememberSaveable {mutableStateOf(false)}
    var isAccountHolderName  by rememberSaveable {mutableStateOf(false)}
    var isAccountType  by rememberSaveable {mutableStateOf(false)}
    var isAccountNo  by rememberSaveable {mutableStateOf(false)}
    var isIFSC  by rememberSaveable {mutableStateOf(false)}
    var isMobNo  by rememberSaveable {mutableStateOf(false)}
    var isEmail  by rememberSaveable {mutableStateOf(false)}
    var isRemarks  by rememberSaveable {mutableStateOf(false)}
    var isNameOnCard  by rememberSaveable {mutableStateOf(false)}
    var isCardNo  by rememberSaveable {mutableStateOf(false)}
    var isExpiryDate  by rememberSaveable {mutableStateOf(false)}
    var isCvv by rememberSaveable {mutableStateOf(false)}
    var isAtmPin  by rememberSaveable {mutableStateOf(false)}
    var isUpiPin  by rememberSaveable {mutableStateOf(false)}
    var isLoginPin  by rememberSaveable {mutableStateOf(false)}
    var isTransactionPin  by rememberSaveable {mutableStateOf(false)}
    var isCancelClick by rememberSaveable {mutableStateOf(false)}
    val selectedDetails = ArrayList<String>()
    val isArchived = viewModel.isArchived
    if (isBankName) {
        selectedDetails.add("${stringResource(id = R.string.bank_name)}: ${uiState.value.bankName}\n")
    }
    if (isAccountHolderName) {
        selectedDetails.add("${stringResource(id = R.string.account_holder_name)}: ${uiState.value.accountHolderName}\n")
    }
    if (isAccountType) {
        selectedDetails.add("${stringResource(id = R.string.account_type)}: ${uiState.value.accountType}\n")
    }
    if (isAccountNo) {
        selectedDetails.add("${stringResource(id = R.string.account_no)}: ${uiState.value.accountNumber}\n")
    }
    if (isIFSC) {
        selectedDetails.add("${stringResource(id = R.string.account_ifsc)}: ${uiState.value.ifscCode}\n")
    }
    if (isMobNo) {
        selectedDetails.add("${stringResource(id = R.string.account_mob_no)}: ${uiState.value.regMobNo}\n")
    }
    if (isEmail) {
        selectedDetails.add("${stringResource(id = R.string.account_email)}: ${uiState.value.regEmail}\n")
    }
    if (isRemarks) {
        selectedDetails.add("${stringResource(id = R.string.remarks)}: ${uiState.value.remarks}\n")
    }
    if (isNameOnCard) {
        selectedDetails.add("${stringResource(id = R.string.name_on_card)}: ${uiState.value.nameOnCard}\n")
    }
    if (isCardNo) {
        selectedDetails.add("${stringResource(id = R.string.card_no)}: ${uiState.value.cardNo}\n")
    }
    if (isExpiryDate) {
        selectedDetails.add("${stringResource(id = R.string.expiry_date)}: ${uiState.value.expiryDate}\n")
    }
    if (isCvv) {
        selectedDetails.add("${stringResource(id = R.string.cvv)}: ${uiState.value.cardCvv}\n")
    }
    if (isAtmPin) {
        selectedDetails.add("${stringResource(id = R.string.atm_pin)}: ${uiState.value.cardPin}\n")
    }
    if (isUpiPin) {
        selectedDetails.add("${stringResource(id = R.string.upi_pin)}: ${uiState.value.upiPin}\n")
    }
    if (isLoginPin) {
        selectedDetails.add("${stringResource(id = R.string.login_pin)}: ${uiState.value.loginPin}\n")
    }
    if (isTransactionPin) {
        selectedDetails.add("${stringResource(id = R.string.transaction_Pin)}: ${uiState.value.transactionPin}\n")
    }
    val shareText = StringBuilder()
    for (field in selectedDetails) {
        shareText.append(field)
    }

    if (isCancelClick) {
        isBankName = false
        isAccountHolderName = false
        isAccountType = false
        isAccountNo = false
        isIFSC = false
        isMobNo = false
        isEmail = false
        isRemarks = false
        isNameOnCard = false
        isCardNo = false
        isExpiryDate = false
        isCvv = false
        isAtmPin = false
        isUpiPin = false
        isLoginPin = false
        isTransactionPin = false
        isCancelClick = false
    }

    Scaffold (
        topBar = {
            OrganizeTopAppBar(
                title = stringResource(id = R.string.bank_account_details),
                canNavigateBack = true,
                isBankAccount = true,
                showMenu = true,
                navigateUp = navigateBack,
                shareSubject = stringResource(id = R.string.bank_account_details),
                shareText = shareText.toString(),
                isArchived = (isArchived == 1),
                isBankName = {isBankName = it},
                isAccountHolderName = {isAccountHolderName = it},
                isAccountType = {isAccountType = it},
                isAccountNo = {isAccountNo = it},
                isIFSC = {isIFSC = it},
                isMobNo = {isMobNo = it},
                isEmail = {isEmail = it},
                isRemarks = {isRemarks = it},
                isNameOnCard = {isNameOnCard = it},
                isCardNo = {isCardNo = it},
                isCvv = {isCvv = it},
                isExpiryDate = {isExpiryDate = it},
                isAtmPin = {isAtmPin = it},
                isUpiPin = {isUpiPin = it},
                isLoginPin = {isLoginPin = it},
                isTransactionPin = {isTransactionPin = it},
                onCancelClick = {isCancelClick = it},
                unArchive = {
                            coroutineScope.launch {
                                viewModel.unArchiveBankAccount()
                                navigateBack()
                            }
                },
                deleteEmail = {
                    coroutineScope.launch {
                        viewModel.deleteBankAccount(uiState.value.toBankAccount())
                        navigateBack()
                    }
                },
                archive = {
                          coroutineScope.launch {
                              viewModel.archiveBankAccount()
                              navigateBack()
                          }
                },
                duplicateEmail = {
                    coroutineScope.launch {
                        viewModel.duplicateBankAccount()
                        navigateBack()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { goToEditScreen(uiState.value.id)},
                shape = MaterialTheme.shapes.medium,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = stringResource(id = R.string.edit_app_account_details))
            }
        }
            ){ innerPadding ->
        BankAccountDetails(
            modifier = modifier.padding(innerPadding),
            bankAccountUiState = uiState.value
        )
    }
}

@Composable
fun BankAccountDetails(
    modifier: Modifier = Modifier,
    bankAccountUiState: BankAccountUiState
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        TitleRow(text = bankAccountUiState.bankName, icon = bankAccountUiState.bankLogo)
        OtherDetails(value = bankAccountUiState.accountHolderName, title = R.string.account_holder_name)
        OtherDetails(value = bankAccountUiState.accountType, title = R.string.account_type)
        OtherDetails(value = bankAccountUiState.accountNumber, title = R.string.account_no)
        OtherDetails(value = bankAccountUiState.ifscCode, title = R.string.account_ifsc)
        OtherDetails(value = bankAccountUiState.regMobNo, title = R.string.account_mob_no)
        OtherDetails(value = bankAccountUiState.regEmail, title = R.string.account_email)
        OtherDetails(value = bankAccountUiState.remarks, title = R.string.remarks)
        if (bankAccountUiState.haveCard) {
            OtherDetails(value = bankAccountUiState.nameOnCard, title = R.string.name_on_card)
            OtherDetails(value = bankAccountUiState.cardNo, title = R.string.card_no)
            OtherDetails(value = bankAccountUiState.expiryDate, title = R.string.expiry_date)
            OtherDetails(value = bankAccountUiState.cardCvv, title = R.string.cvv)
            OtherDetails(value = bankAccountUiState.cardPin, title = R.string.atm_pin)
        }
        if (bankAccountUiState.haveUpi) {
            OtherDetails(value = bankAccountUiState.upiPin, title = R.string.upi_pin)
        }
        if (bankAccountUiState.haveBankingApp) {
            OtherDetails(value = bankAccountUiState.loginPin, title = R.string.login_pin)
            OtherDetails(value = bankAccountUiState.transactionPin, title = R.string.transaction_Pin)
        }
    }
}