package com.example.currencyconverterapp.data.usecase

import com.example.currencyconverterapp.data.local.repository.LocalRepository
import com.example.currencyconverterapp.data.repository.Repository
import javax.inject.Inject

class FetchCurrenciesUsecase @Inject constructor(private val repository: LocalRepository) {
    suspend operator fun invoke() = repository.storeCurrencies
}
