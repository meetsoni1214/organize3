package com.example.organize3.splashScreen

import androidx.compose.runtime.mutableStateOf
import com.example.organize3.MyViewModel
import com.example.organize3.model.service.AccountService
import com.example.organize3.model.service.LogService
import com.example.organize3.navigation.OrganizeDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
): MyViewModel(logService){
    val showError = mutableStateOf(false)

    fun onAppStart(openAndPopUp: (String, String) -> Unit) {
        showError.value = false
        if (accountService.hasUser) openAndPopUp(OrganizeDestination.Categories.route, OrganizeDestination.SplashScreen.route)
        else openAndPopUp(OrganizeDestination.LoginScreen.route, OrganizeDestination.SplashScreen.route)
    }
}