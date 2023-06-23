package com.example.organize3.model.service

interface LogService {
    fun logNonFatalCrash(throwable: Throwable)
}