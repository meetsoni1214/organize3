@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.organize3.bankAccounts

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.organize3.AppViewModelProvider
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.R
import com.example.organize3.appUi.BankAccountUiState
import kotlinx.coroutines.launch
import java.util.*


@Composable
fun AddOtherDetailsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    navigateUp: () -> Unit,
    navigateBack: () -> Unit,
    viewModel: BankAccountEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val bankAccountUiState = viewModel.bankAccountUiState
    Scaffold (
        topBar = {
            OrganizeTopAppBar(
                title = stringResource(id = R.string.other_details),
                canNavigateBack = canNavigateBack,
                navigateUp = navigateUp
            )
        }
            ){ innerPadding ->
        AddOtherDetailsBody(
            modifier = modifier.padding(innerPadding),
            bankAccountUiState = bankAccountUiState,
            onValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveBankAccount()
                }
                navigateBack()
            },
//            goToAddBankScreen = navigateUp
        )
    }
}

@Composable
fun AddOtherDetailsBody(
    modifier: Modifier = Modifier,
    bankAccountUiState: BankAccountUiState,
    onValueChange:(BankAccountUiState) -> Unit,
//    goToAddBankScreen: () -> Unit,
    onSaveClick:() -> Unit
) {
    Column (
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
            ) {
        AddOtherDetailsFirstRow(
            bankAccountUiState = bankAccountUiState,
//            goToAddBankScreen = goToAddBankScreen
        )
        BankOtherTextFields(
            bankAccountUiState = bankAccountUiState,
            onValueChange = onValueChange
        )
        AtmCardDetailsQues(
            bankAccountUiState = bankAccountUiState,
            onValueChange = onValueChange
        )
        UpiQuestion(
            bankAccountUiState = bankAccountUiState,
            onValueChange = onValueChange
        )
        BankingAppQues(
            bankAccountUiState = bankAccountUiState,
            onValueChange = onValueChange
        )
        Button(
            onClick = onSaveClick,
            enabled = bankAccountUiState.actionEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Text(text = stringResource(id = R.string.save_details))
        }
    }
}

@Composable
fun AddOtherDetailsFirstRow(
    modifier: Modifier = Modifier,
//    goToAddBankScreen:() -> Unit,
    bankAccountUiState: BankAccountUiState
) {
    Row(modifier = modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        OutlinedTextField(
            value = bankAccountUiState.bankName,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = {  }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            label = { Text(text = stringResource(id = R.string.bank_name))}
        )
        Image(painter = painterResource(id = bankAccountUiState.bankLogo), contentDescription = stringResource(
            id = R.string.bank_logo
        ),
            modifier = Modifier
                .clip(CircleShape)
                .width(60.dp)
                .height(60.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun BankOtherTextFields(
    modifier: Modifier = Modifier,
    bankAccountUiState: BankAccountUiState,
    onValueChange: (BankAccountUiState) -> Unit
) {
    val accountType = listOf(stringResource(id = R.string.saving), stringResource(id = R.string.current))
    var expanded by remember { mutableStateOf(false) }
    val selectedOptionText = bankAccountUiState.accountType
    val focusManager = LocalFocusManager.current
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = bankAccountUiState.accountHolderName,
            onValueChange = {onValueChange(bankAccountUiState.copy(accountHolderName = it))},
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.account_holder_name))}
        )
        OutlinedTextField(
            value = selectedOptionText,
            readOnly = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            onValueChange = {
                onValueChange(bankAccountUiState.copy(accountType = selectedOptionText))
                            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                }
            ,
            label = { Text(text = stringResource(id = R.string.account_type))},
            trailingIcon = {
                IconButton(onClick = {expanded = !expanded }) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false},
            modifier = Modifier
                .width(with(LocalDensity.current){textFieldSize.width.toDp()})
        ) {
            accountType.forEach { label ->
                DropdownMenuItem(
                    text = { Text(text = label)},
                    onClick = {
                        onValueChange(bankAccountUiState.copy(accountType = label))
                        expanded = false
                    })
            }
        }
        OutlinedTextField(
            value = bankAccountUiState.accountNumber,
            onValueChange = {onValueChange(bankAccountUiState.copy(accountNumber = it))},
            singleLine = true,
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            modifier =  Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.account_no))},
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Characters
            )
        )
        OutlinedTextField(
            value = bankAccountUiState.ifscCode,
            onValueChange = {onValueChange(bankAccountUiState.copy(ifscCode = it))},
            modifier =  Modifier.fillMaxWidth(),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            singleLine = true,
            label = { Text(text = stringResource(id = R.string.account_ifsc))},
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Characters)
        )
        OutlinedTextField(
            value = bankAccountUiState.regMobNo,
            onValueChange = {onValueChange(bankAccountUiState.copy(regMobNo = it))},
            modifier =  Modifier.fillMaxWidth(),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            singleLine = true,
            label = { Text(text = stringResource(id = R.string.account_mob_no))},
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Phone)
        )
        OutlinedTextField(
            value = bankAccountUiState.regEmail,
            onValueChange = {onValueChange(bankAccountUiState.copy(regEmail = it))},
            modifier =  Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            label = { Text(text = stringResource(id = R.string.account_email))},
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(
            value = bankAccountUiState.remarks,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.clearFocus()
                }
            ),
            onValueChange = {onValueChange(bankAccountUiState.copy(remarks = it))},
            modifier =  Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.remarks))},
        )
    }
}

@Composable
fun AtmCardDetailsQues(
    modifier: Modifier = Modifier,
    bankAccountUiState: BankAccountUiState,
    onValueChange: (BankAccountUiState) -> Unit
) {
    val hasAtmCard = bankAccountUiState.haveCard
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.card_details_question),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelLarge
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            RadioButton(selected = hasAtmCard, onClick = {
                onValueChange(bankAccountUiState.copy(haveCard = true))
            })
            Text(
                text = stringResource(id = R.string.yes),
                modifier = Modifier
                    .clickable(onClick = {
                        onValueChange(bankAccountUiState.copy(haveCard = true))
                    })
            )
            Spacer(modifier = Modifier.width(22.dp))
            RadioButton(selected = !hasAtmCard, onClick = {
                onValueChange(bankAccountUiState.copy(haveCard = false))
            })
            Text(
                text = stringResource(id = R.string.no),
                modifier = Modifier
                    .clickable(onClick = {
                        onValueChange(bankAccountUiState.copy(haveCard = false))
                    })
            )
        }
        if (hasAtmCard) {
            AtmCardDetails(
                bankAccountUiState = bankAccountUiState,
                onValueChange = onValueChange
            )
        }
    }
}

@Composable
fun AtmCardDetails(
    modifier: Modifier = Modifier,
    bankAccountUiState: BankAccountUiState,
    onValueChange: (BankAccountUiState) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val mCalendar = Calendar.getInstance()
    val mYear = mCalendar.get(Calendar.YEAR)
    val mMonth = mCalendar.get(Calendar.MONTH)
    val mDay = mCalendar.get(Calendar.DAY_OF_MONTH)
    mCalendar.time = Date()
    val mDate = bankAccountUiState.expiryDate
    val mDatePickerDialog = DatePickerDialog(
        context,
        AlertDialog.THEME_HOLO_DARK,
        {_: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            onValueChange(bankAccountUiState.copy(expiryDate = "${mMonth+1}/${mYear}"))
        },mYear, mMonth, mDay
    )
    val screenWidth = configuration.screenWidthDp.dp
    val maxCardNo = 16
    val maxCvv = 3
    val maxPin = 4
    val cardNo = bankAccountUiState.cardNo
    val cardCvv = bankAccountUiState.cardCvv
    val cardAtm = bankAccountUiState.cardPin
    var passwordVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var cvvVisible by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        OutlinedTextField(
            value = bankAccountUiState.nameOnCard,
            onValueChange = {onValueChange(bankAccountUiState.copy(nameOnCard = it))},
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text(text = stringResource(id = R.string.name_on_card))},
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Characters)
        )
        OutlinedTextField(
            value = cardNo,
            onValueChange = {
                if (it.length <= maxCardNo) {
                    onValueChange(bankAccountUiState.copy(cardNo = it))
                }
                else Toast.makeText(context,"Card Number should not be more than 16 digits", Toast.LENGTH_SHORT).show()
            },
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.clearFocus()
                }
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text(text = stringResource(id = R.string.card_no))},
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = mDate,
                onValueChange = {onValueChange(bankAccountUiState.copy(expiryDate = it))},
                readOnly = true,
                singleLine = true,
                modifier = Modifier
                    .width(screenWidth / 2),
                label = { Text(text = stringResource(id = R.string.expiry_date))},
                trailingIcon = {
                    IconButton(onClick = {
                        mDatePickerDialog.show()
                    }) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Date_Picker_Icon")
                    }
                }
            )
            OutlinedTextField(
                value = cardCvv,
                onValueChange = {if (it.length <= maxCvv) {
                    onValueChange(bankAccountUiState.copy(cardCvv = it))
                }
                    else Toast.makeText(context, "Card CVV should not be more than 3 digits", Toast.LENGTH_SHORT).show()},
                singleLine = true,
                modifier = Modifier
                    .width(screenWidth / 2),
                label = { Text(text = stringResource(id = R.string.cvv))},
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                visualTransformation = if (cvvVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.NumberPassword),
                trailingIcon = {
                    val image = if (cvvVisible) {
                        R.drawable.visibility_off
                    }else {
                        R.drawable.visibility
                    }
                    val description = if (cvvVisible) "Hide Password" else "Show Password"
                    IconButton(onClick = {cvvVisible = !cvvVisible}) {
                        Icon(painter = painterResource(id = image), contentDescription = description)
                    }
                }
            )
        }
        OutlinedTextField(
            value = cardAtm,
            onValueChange = {if (it.length <= maxPin) {
                onValueChange(bankAccountUiState.copy(cardPin = it))
            }
            else Toast.makeText(context, "Card Atm Pin should not be more than 4 digits", Toast.LENGTH_SHORT).show()},
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.clearFocus()
                }
            ),
            label = { Text(text = stringResource(id = R.string.atm_pin))},
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.NumberPassword),
            trailingIcon = {
                val image = if (passwordVisible) {
                    R.drawable.visibility_off
                }else {
                    R.drawable.visibility
                }
                val description = if (passwordVisible) "Hide Password" else "Show Password"
                IconButton(onClick = {passwordVisible = !passwordVisible}) {
                    Icon(painter = painterResource(id = image), contentDescription = description)
                }
            }
        )
    }
}

@Composable
fun UpiQuestion(
    modifier: Modifier = Modifier,
    bankAccountUiState: BankAccountUiState,
    onValueChange: (BankAccountUiState) -> Unit
) {
    val hasUpi = bankAccountUiState.haveUpi
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.upi_question),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelLarge
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            RadioButton(selected = hasUpi, onClick = {
                onValueChange(bankAccountUiState.copy(haveUpi = true))
            })
            Text(
                text = stringResource(id = R.string.yes),
                modifier = Modifier
                    .clickable(onClick = {
                        onValueChange(bankAccountUiState.copy(haveUpi = true))
                    })
            )
            Spacer(modifier = Modifier.width(22.dp))
            RadioButton(selected = !hasUpi, onClick = {
                onValueChange(bankAccountUiState.copy(haveUpi = false))
            })
            Text(
                text = stringResource(id = R.string.no),
                modifier = Modifier
                    .clickable(onClick = {
                        onValueChange(bankAccountUiState.copy(haveUpi = false))
                    })
            )
        }
        if (hasUpi) {
            UpiCardDetails(
                bankAccountUiState = bankAccountUiState,
                onValueChange = onValueChange
            )
        }
    }
}

@Composable
fun UpiCardDetails(
    modifier: Modifier = Modifier,
    bankAccountUiState: BankAccountUiState,
    onValueChange: (BankAccountUiState) -> Unit
) {
    var passwordVisible by rememberSaveable {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val maxUpi = 6
    val upiPin = bankAccountUiState.upiPin
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = upiPin,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.NumberPassword),
            label = { Text(text = stringResource(id = R.string.upi_pin))},
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.clearFocus()
                }
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                           val image = if (passwordVisible) {
                               R.drawable.visibility_off
                           }else {
                               R.drawable.visibility
                           }
                val description = if (passwordVisible) "hide Password" else "show Password"
                IconButton(onClick = {passwordVisible = !passwordVisible}) {
                    Icon(painter = painterResource(id = image), contentDescription = description)
                }
            },
            onValueChange = {if (it.length <= maxUpi) {
                onValueChange(bankAccountUiState.copy(upiPin = it))
            } else Toast.makeText(context, "UPI Pin should not be more than six digits!", Toast.LENGTH_SHORT).show()})
    }
}

@Composable
fun BankingAppQues(
    modifier: Modifier = Modifier,
    bankAccountUiState: BankAccountUiState,
    onValueChange: (BankAccountUiState) -> Unit
) {
    val hasApp = bankAccountUiState.haveBankingApp
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.mobile_banking_app_question),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelLarge
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            RadioButton(selected = hasApp, onClick = {
                onValueChange(bankAccountUiState.copy(haveBankingApp = true)) })
            Text(
                text = stringResource(id = R.string.yes),
                modifier = Modifier
                    .clickable(onClick = {
                        onValueChange(bankAccountUiState.copy(haveBankingApp = true))
                    })
            )
            Spacer(modifier = Modifier.width(22.dp))
            RadioButton(selected = !hasApp, onClick = {
                onValueChange(bankAccountUiState.copy(haveBankingApp = false)) })
            Text(
                text = stringResource(id = R.string.no),
                modifier = Modifier
                    .clickable(onClick = {
                        onValueChange(bankAccountUiState.copy(haveBankingApp = false))
                    })
            )
        }
        if (hasApp) {
            BankingAppDetails(
                bankAccountUiState = bankAccountUiState,
                onValueChange = onValueChange
            )
        }
    }
}

@Composable
fun BankingAppDetails(
    modifier: Modifier = Modifier,
    bankAccountUiState: BankAccountUiState,
    onValueChange: (BankAccountUiState) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by rememberSaveable {
        mutableStateOf(false)
    }
    Column(modifier = modifier
        .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = bankAccountUiState.loginPin,
            label = { Text(text = stringResource(id = R.string.login_pin))},
            modifier = Modifier.fillMaxWidth(),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            singleLine = true,
            onValueChange = {onValueChange(bankAccountUiState.copy(loginPin = it))})
        OutlinedTextField(
            value = bankAccountUiState.transactionPin,
            onValueChange = {onValueChange(bankAccountUiState.copy(transactionPin = it))},
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.clearFocus()
                }
            ),
            label = { Text(text = stringResource(id = R.string.transaction_Pin))},
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible){
                    R.drawable.visibility_off
                }else {
                    R.drawable.visibility
                }
                val description = if (passwordVisible) {
                    "Hide Password"
                }else {
                    "Show Password"
                }
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(painter = painterResource(id = image), contentDescription = description)
                }
            }
        )
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun OtherTextFieldsPreview() {
//    AddOtherDetailsBody()
//}
