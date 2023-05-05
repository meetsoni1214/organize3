package com.example.organize3.data.application

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.organize3.R

@Entity(tableName = "application_account")
data class ApplicationAccount (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "logo") val appLogo: Int = R.drawable.website_logo,
    @ColumnInfo(name = "title") val accountTitle: String,
    @ColumnInfo(name = "username") val accountUsername: String,
    @ColumnInfo(name = "password") val accountPassword: String,
    @ColumnInfo(name = "remarks") val accountRemarks: String,
    @ColumnInfo(name = "isArchived") val isArchived: Int = 0
        )
