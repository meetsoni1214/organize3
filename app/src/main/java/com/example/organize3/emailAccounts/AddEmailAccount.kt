@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.organize3.emailAccounts

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.organize3.R
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.organize3.AppViewModelProvider
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.appUi.EmailUiState
import kotlinx.coroutines.launch


@Composable
fun AddEmailAccountScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp:() -> Unit,
    navigateBack: () -> Unit,
    viewModel: EmailEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            OrganizeTopAppBar(
                title = stringResource(id = R.string.enter_bank_details),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp)
        }
    ) { innerPadding ->
        EmailEntryBody(
            emailUiState = viewModel.emailUiState,
            onEmailValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveEmail()
                }
                navigateBack()
            },
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
fun EmailEntryBody(
    emailUiState: EmailUiState,
    onEmailValueChange: (EmailUiState) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .padding(20.dp)
        .verticalScroll(rememberScrollState())) {
        FirstRow(emailUiState = emailUiState, onEmailValueChange = onEmailValueChange)
        OtherTextFields(emailUiState = emailUiState, onEmailValueChange = onEmailValueChange)
        Button(
            onClick = onSaveClick,
            enabled = emailUiState.actionEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.save_details))
        }
    }
}

@Composable
fun OtherTextFields(
    modifier: Modifier = Modifier,
    emailUiState: EmailUiState,
    onEmailValueChange: (EmailUiState) -> Unit = {},
    enabled: Boolean = true
    ) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    Column(modifier = modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = emailUiState.email,
            onValueChange = { onEmailValueChange(emailUiState.copy(email = it))},
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            enabled = enabled,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email),
            label = { Text(stringResource(R.string.email_id)) })
        OutlinedTextField(
            value = emailUiState.password,
            onValueChange = { onEmailValueChange(emailUiState.copy(password = it))},
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            enabled = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                           val image = if (passwordVisible) {
                               R.drawable.visibility_off
                           }else {
                               R.drawable.visibility
                           }
                val description = if (passwordVisible) "Hide Password" else "Show Password"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(painter = painterResource(id = image), contentDescription = description)
                }
                           },
            label = { Text(stringResource(R.string.password)) })
        OutlinedTextField(
            value = emailUiState.remarks,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.clearFocus()
                }
            ),
            onValueChange = {onEmailValueChange(emailUiState.copy(remarks = it))},
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
            enabled = enabled,
            label = { Text(stringResource(R.string.remarks)) })
    }
}

@Composable
fun FirstRow(
    modifier: Modifier = Modifier,
    emailUiState: EmailUiState,
    onEmailValueChange: (EmailUiState) -> Unit,
    enabled: Boolean = true) {
    val focusManager = LocalFocusManager.current
    Row(modifier = modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp)) {
        OutlinedTextField(
            value = emailUiState.title,
            onValueChange = {onEmailValueChange(emailUiState.copy(title = it))},
            enabled = enabled,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            singleLine = true,
            label = { Text(stringResource(R.string.title)) })
        Image(painter = painterResource(id = R.drawable.email_icon),
            contentDescription = stringResource(
                id = R.string.email_icon
            ),
            modifier = Modifier
                .clip(CircleShape)
                .width(60.dp)
                .height(60.dp),
            contentScale = ContentScale.Crop,
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun EmailEntryBodyPreview() {
//    EmailEntryBody()
//}