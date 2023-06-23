package com.example.organize3.model.service.module

import com.example.organize3.model.service.AccountService
import com.example.organize3.model.service.LogService
import com.example.organize3.model.service.impl.AccountServiceImpl
import com.example.organize3.model.service.impl.LogServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds abstract fun provideLogService(impl: LogServiceImpl): LogService
}