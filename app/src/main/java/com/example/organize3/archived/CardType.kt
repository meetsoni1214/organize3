package com.example.organize3.archived

sealed class CardType {
    object EmailAccountCard : CardType()
    object ApplicationAccountCard: CardType()
    object BankAccountCard: CardType()
    object NoteCard: CardType()
}