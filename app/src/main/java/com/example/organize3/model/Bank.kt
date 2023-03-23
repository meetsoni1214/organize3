package com.example.organize3.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import com.example.organize3.R

data class Bank (
        val bankName: String,
        @DrawableRes val bankLogo: Int = R.drawable.bank_image_2
        )