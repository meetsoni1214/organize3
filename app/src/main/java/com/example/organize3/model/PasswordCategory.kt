package com.example.organize3.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class PasswordCategory(
    @StringRes val stringResourceId: Int,
    @DrawableRes val ImageResourceID: Int
)