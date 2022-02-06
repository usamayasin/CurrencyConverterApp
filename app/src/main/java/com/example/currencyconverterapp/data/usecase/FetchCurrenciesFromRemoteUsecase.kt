package com.example.currencyconverterapp.data.usecase

import com.example.currencyconverterapp.data.repository.Repository
import javax.inject.Inject

class FetchCurrenciesFromRemoteUsecase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke() = repository.getCurrencies()
}