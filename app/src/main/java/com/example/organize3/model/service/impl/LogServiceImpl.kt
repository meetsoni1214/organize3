package com.example.organize3.model.service.impl

import com.example.organize3.model.service.LogService
import javax.inject.Inject

class LogServiceImpl @Inject constructor(): LogService {
    override fun logNonFatalCrash(throwable: Throwable) {

    }
}