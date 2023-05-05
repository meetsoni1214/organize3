package com.example.organize3.navigation


sealed class OrganizeDestination(val route: String) {
    object Categories: OrganizeDestination("categories")
    object BankAccounts: OrganizeDestination("bankAccounts")
    object ApplicationAccounts: OrganizeDestination("applicationAccounts")
    object EmailAccounts: OrganizeDestination("emailAccounts")
    object AddBankAccountScreen: OrganizeDestination("bankAccount_one")
    object AddEmailAccountScreen: OrganizeDestination("emailAccount_one")
    object EmailAccountDetailScreen: OrganizeDestination("emailAccount_details")
    object EmailEditScreen: OrganizeDestination("emailAccount_edit")
    object AddApplicationAccountScreen: OrganizeDestination("applicationAccount_one")
    object ApplicationAccountDetailScreen: OrganizeDestination("applicationAccount_two")
    object ApplicationEditScreen: OrganizeDestination("applicationAccount_edit")
    object AddOtherDetailsScreen: OrganizeDestination("bank_other_details_add")
    object BankAccountDetailsScreen: OrganizeDestination("bank_account_details")
    object BankAccountEditScreen: OrganizeDestination("bank_account_edit")
    object EditBankScreen: OrganizeDestination("edit_bank")
    object LoginScreen: OrganizeDestination("login_screen")
    object RegisterScreen: OrganizeDestination("register_screen")
    object Notes: OrganizeDestination("notes_home_screen")
    object AddNote: OrganizeDestination("add_note_screen")
    object ArchivedScreen: OrganizeDestination("archived_screen")

    fun withArgs(vararg args: Int): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
    }

