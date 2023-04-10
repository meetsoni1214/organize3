package com.example.organize3.presentation.sign_in

data class SignInState (
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
        )