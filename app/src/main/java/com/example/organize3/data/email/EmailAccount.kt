package com.example.organize3.data.email

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.organize3.R
import com.example.organize3.Searchable

@Entity(tableName = "emailaccount")
data class EmailAccount(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val accountTitle: String,
    @ColumnInfo(name = "email") val accountEmail: String,
    @ColumnInfo(name = "password") val accountPassword: String,
    @ColumnInfo(name = "remarks") val accountRemarks: String,
    @ColumnInfo(name = "isArchived") val isArchived: Int = 0
) : Searchable {
    override fun doesMatchSearchQuery(query: String): Boolean {
        return accountTitle.contains(query.trim(), ignoreCase = true)
    }
 }