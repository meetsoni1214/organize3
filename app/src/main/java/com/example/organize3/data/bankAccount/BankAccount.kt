package com.example.organize3.data.bankAccount

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.organize3.R
import com.example.organize3.Searchable
import com.example.organize3.model.Bank

@Entity(tableName = "bankaccount")
data class BankAccount (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "bank_name") val bankName: String = "",
    @ColumnInfo(name = "bank_logo") val bankLogo: Int = R.drawable.bank_image_2,
    @ColumnInfo(name = "holder_name") val accountHolderName: String = "",
    @ColumnInfo(name = "account_type") val accountType: String = "",
    @ColumnInfo(name = "account_number") val accountNumber: String = "",
    @ColumnInfo(name = "ifsc") val ifscCode: String = "",
    @ColumnInfo(name = "reg_mob_no") val regMobNo: String = "",
    @ColumnInfo(name = "reg_email") val regEmail: String = "",
    @ColumnInfo(name = "remarks") val remarks: String = "",
    @ColumnInfo(name = "have_card") val haveCard: Boolean = true,
    @ColumnInfo(name = "name_on_card") val nameOnCard: String = "",
    @ColumnInfo(name = "card_no") val cardNo: String = "",
    @ColumnInfo(name = "exp_date") val expiryDate: String= "",
    @ColumnInfo(name = "cvv") val cardCvv: String = "",
    @ColumnInfo(name = "atm_pin") val cardPin: String = "",
    @ColumnInfo(name = "have_upi") val haveUpi: Boolean = true,
    @ColumnInfo(name = "upi_pin") val upiPin: String = "",
    @ColumnInfo(name = "have_banking_app") val haveBankingApp: Boolean = true,
    @ColumnInfo(name = "login_pin") val loginPin: String = "",
    @ColumnInfo(name = "t_pin") val transactionPin: String = "",
    @ColumnInfo(name = "isArchived") val isArchived: Int = 0
        ) : Searchable {
    override fun doesMatchSearchQuery(query: String): Boolean {
        return bankName.contains(query.trim(), ignoreCase = true) || accountHolderName.contains(query.trim(), ignoreCase = true)
    }
}