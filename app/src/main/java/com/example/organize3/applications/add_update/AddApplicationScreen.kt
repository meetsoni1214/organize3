@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.organize3.applications.add_update

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.organize3.AppViewModelProvider
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.R
import com.example.organize3.applications.details.ApplicationUiState
import com.example.organize3.data.application.ApplicationAccount
import kotlinx.coroutines.launch

@Composable
fun AddApplicationScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    navigateUp: () -> Unit,
    navigateBack: () -> Unit,
    uiState: ApplicationUiState,
    onTitleChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onRemarksChanged: (String) -> Unit,
    onSaveClick: (ApplicationAccount) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold (
        topBar = {
            OrganizeTopAppBar(
                title = stringResource(id = R.string.application_account_details),
                canNavigateBack = canNavigateBack,
                navigateUp = navigateUp
            )
        }
            ) { innerPadding ->
        ApplicationEntryBody(
            modifier = modifier.padding(innerPadding),
            applicationUiState = uiState,
            onTitleChanged = onTitleChanged,
            onUsernameChanged = onUsernameChanged,
            onRemarksChanged = onRemarksChanged,
            onPasswordChanged = onPasswordChanged,
            onSaveClick = onSaveClick
        )
    }
}

@Composable
fun ApplicationEntryBody(
    applicationUiState: ApplicationUiState,
    onTitleChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onRemarksChanged: (String) -> Unit,
    onSaveClick: (ApplicationAccount) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column (
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
            ){
        AddAppFirstRow(
            title = applicationUiState.title,
            onTitleChanged = onTitleChanged
        )
        AppOtherTextFields(
            username = applicationUiState.username,
            password = applicationUiState.password,
            remarks = applicationUiState.remarks,
            onRemarksChanged = onRemarksChanged,
            onPasswordChanged = onPasswordChanged,
            onUsernameChanged = onUsernameChanged
        )
        Button(
            onClick = {
                      if (applicationUiState.title.isNotEmpty() && applicationUiState.username.isNotEmpty()) {
                          onSaveClick(
                              ApplicationAccount().apply {
                                  this.title = applicationUiState.title
                                  this.username = applicationUiState.username
                                  this.password = applicationUiState.password
                                  this.remarks = applicationUiState.remarks
                              }
                          )
                      } else {
                          Toast.makeText(
                              context,
                              "Fields cannot be empty.",
                              Toast.LENGTH_SHORT
                          ).show()
                      }
            },
            enabled = true,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.save_details))
        }
    }
}

@Composable
fun AddAppFirstRow(
    modifier: Modifier = Modifier,
    title: String,
    onTitleChanged: (String) -> Unit,
    enabled: Boolean = true
) {
    val focusManager = LocalFocusManager.current
    Row(modifier = modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = {onTitleChanged(it)},
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            singleLine = true,
            enabled = enabled,
            label = { Text(stringResource(R.string.title)) })
        Image(painter = painterResource(id = R.drawable.website_logo),
            contentDescription = stringResource(
                id = R.string.appLogo
            ),
            modifier = Modifier
                .clip(CircleShape)
                .width(60.dp)
                .height(60.dp),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun AppOtherTextFields(
    modifier: Modifier = Modifier,
    username: String,
    onUsernameChanged: (String) -> Unit,
    password: String,
    onPasswordChanged: (String) -> Unit,
    remarks: String,
    onRemarksChanged: (String) -> Unit,
    enabled: Boolean = true,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = {onUsernameChanged(it)},
            singleLine = true,
            enabled = enabled,
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            label = {Text(stringResource(id = R.string.facebook_login_title))}
        )
        OutlinedTextField(
            value = password,
            onValueChange = {onPasswordChanged(it)},
            singleLine = true,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
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
                val description = if (passwordVisible) "Hide password" else "Show Password"
                IconButton(onClick = { passwordVisible = !passwordVisible}) {
                    Icon(painter = painterResource(id = image), contentDescription = description)
                }
            },
            label = {Text(stringResource(id = R.string.password))}
        )
        OutlinedTextField(
            value = remarks,
            onValueChange = {onRemarksChanged(it)},
            singleLine = true,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.clearFocus()
                }
            ),
            modifier = Modifier.fillMaxWidth(),
            label = {Text(stringResource(id = R.string.remarks))}
        )
    }
}

