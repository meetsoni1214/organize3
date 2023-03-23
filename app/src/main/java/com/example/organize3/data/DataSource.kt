package com.example.organize3.data

import com.example.organize3.R
import com.example.organize3.model.Bank
import com.example.organize3.model.PasswordCategory

object  DataSource {
    val Categories = listOf<PasswordCategory>(
        PasswordCategory(R.string.bank_category, R.drawable.bank_category_image),
        PasswordCategory(R.string.application_category, R.drawable.social_media_category_image),
        PasswordCategory(R.string.email_category, R.drawable.email_account_category_image)
    )
    val Banks = listOf<Bank>(
        Bank("AMBARNATH JAI-HIND CO-OP.BANK LTD"),
        Bank("AU Small Finance Bank"),
        Bank("Abhyudaya Co-Operative Bank"),
        Bank("ARUNACHAL PRADESH RURAL BANK"),
        Bank("Adarsh Co-operative Bank"),
        Bank("Ahmednagar Merchant's Co-operative Bank"),
        Bank("Ahmednagar Shahar Sahakari Bank Ltd"),
        Bank("Airtel Payments Bank"),
        Bank("Ajantha Urban Co-Op Bank Ltd"),
        Bank("Akhand Anand Co Operative Bank ltd."),
        Bank("Allahabad Bank (Now Indian Bank)"),
        Bank("Ambajogai Peoples Co-Operative Bank Ltd"),
        Bank("Amreli Jilla Madhyastha Sahakari Bank Ltd"),
        Bank("Andhra Bank(Now: Union Bank)"),
        Bank("Andhra Pradesh Grameena Vikas Bank"),
        Bank("Andhra Pradesh Mahesh Co-Operative Urban Bank Ltd."),
        Bank("Andhra Pragathi Grameena Bank"),
        Bank("Apna Sahakari Bank"),
        Bank("Arihant Urban Co-Operative Bank Ltd"),
        Bank("Arvind Sahakari Bank Ltd"),
        Bank("Assam Gramin Vikash Bank"),
        Bank("Axis Bank", R.drawable.axis_bank),
        Bank("The Kalupur Commercial Co-operative Bank", R.drawable.kalupur_bank_logo),
        Bank("State Bank of India", R.drawable.sbi_logo),
        Bank("Bank of India", R.drawable.boi),
        Bank("Bank of Baroda", R.drawable.bank_of_baroda_logo),
        Bank("Union Bank of India", R.drawable.union_bank),
        Bank("ICICI Bank", R.drawable.icici_logo)
    )
}