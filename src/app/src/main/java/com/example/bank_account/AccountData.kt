package com.example.bank_account

class AccountData constructor(id: Int, accountName: String, amount: Double, iban: String, currency: String) {
    var id = id
    var name = accountName
    var amount = amount
    var iban = iban
    var currency = currency
}