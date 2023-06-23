package com.example.organize3.presentation.sign_in

data class SignInState (
    val email: String = "",
    val password: String = "",
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
        )