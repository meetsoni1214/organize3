package com.example.organize3.appUi

import com.example.organize3.R
import com.example.organize3.data.bankAccount.BankAccount

data class BankAccountUiState (
    val id: Int = 0,
    val bankName: String = "",
    val bankLogo: Int = R.drawable.bank_image_2,
    val accountHolderName: String = "",
    val accountType: String = "",
    val accountNumber: String = "",
    val ifscCode: String = "",
    val regMobNo: String = "",
    val regEmail: String = "",
    val remarks: String = "",
    val haveCard: Boolean = true,
    val nameOnCard: String = "",
    val cardNo: String = "",
    val expiryDate: String = "",
    val cardCvv: String = "",
    val cardPin: String = "",
    val haveUpi: Boolean = true,
    val upiPin: String = "",
    val haveBankingApp: Boolean = true,
    val loginPin: String = "",
    val transactionPin: String = "",
    val actionEnabled: Boolean = false
        )
// Extension function to convert [BankAccountUiState] to [BankAccount].
fun BankAccountUiState.toBankAccount(): BankAccount = BankAccount(
    id = id,
    bankName = bankName,
    bankLogo = bankLogo,
    accountHolderName = accountHolderName,
    accountType = accountType,
    accountNumber = accountNumber,
    ifscCode = ifscCode,
    regMobNo = regMobNo,
    regEmail = regEmail,
    remarks = remarks,
    haveCard = haveCard,
    nameOnCard = nameOnCard,
    cardNo = cardNo,
    expiryDate = expiryDate,
    cardCvv = cardCvv,
    cardPin = cardPin,
    haveUpi = haveUpi,
    upiPin = upiPin,
    haveBankingApp = haveBankingApp,
    loginPin = loginPin,
    transactionPin = transactionPin
)

// Extension function to convert [BankAccount] to [BankAccountUiState]
fun BankAccount.toBankAccountUiState(actionEnabled: Boolean = false): BankAccountUiState = BankAccountUiState(
    id = id,
    bankName = bankName,
    bankLogo = bankLogo,
    accountHolderName = accountHolderName,
    accountType = accountType,
    accountNumber = accountNumber,
    ifscCode = ifscCode,
    regMobNo = regMobNo,
    regEmail = regEmail,
    remarks = remarks,
    haveCard = haveCard,
    nameOnCard = nameOnCard,
    cardNo = cardNo,
    expiryDate = expiryDate,
    cardCvv = cardCvv,
    cardPin = cardPin,
    haveUpi = haveUpi,
    upiPin = upiPin,
    haveBankingApp = haveBankingApp,
    loginPin = loginPin,
    transactionPin = transactionPin
)

fun BankAccountUiState.isValid():Boolean
{
    return  accountHolderName.isNotBlank() && accountNumber.isNotBlank()
}