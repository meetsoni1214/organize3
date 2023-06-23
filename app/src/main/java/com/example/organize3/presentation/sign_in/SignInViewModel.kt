package com.example.organize3.presentation.sign_in

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.organize3.MyViewModel
import com.example.organize3.R
import com.example.organize3.isValidEmail
import com.example.organize3.model.service.AccountService
import com.example.organize3.model.service.LogService
import com.example.organize3.navigation.OrganizeDestination
import com.example.organize3.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
): MyViewModel(logService){

    var uiState = mutableStateOf(SignInState())
        private set

    private val email
    get() = uiState.value.email
    private val password
    get() = uiState.value.password

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(R.string.email_error)
            return
        }

        if (password.isBlank()) {
            SnackbarManager.showMessage(R.string.empty_password_error)
            return
        }

        launchCatching {
            accountService.authenticate(email, password)
            openAndPopUp(OrganizeDestination.Categories.route, OrganizeDestination.LoginScreen.route)
        }
    }
    fun onForgotPasswordClick() {
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(R.string.email_error)
            return
        }
        launchCatching {
            accountService.sendRecoveryEmail(email)
            SnackbarManager.showMessage(R.string.recovery_email_sent)
        }
    }
//    fun onSignInResult(result: SignInResult) {
//        _state.update { it.copy(
//            isSignInSuccessful = result.data != null,
//            signInError = result.errorMessage
//        ) }
//    }
//    fun resetState() {
//        _state.update { SignInState() }
//    }
}