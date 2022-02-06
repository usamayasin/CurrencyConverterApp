package com.example.currencyconverterapp.data.repository

import com.example.currencyconverterapp.data.DataState
import com.example.currencyconverterapp.data.model.CurrenciesResponse
import com.example.currencyconverterapp.data.model.ExchangeRatesResponse
import kotlinx.coroutines.flow.Flow

/**
 * Repository is an interface data layer to handle communication with any data source such as Server or local database.
 * @see [RepositoryImpl] for implementation of this class to utilize APIService.
 */
interface Repository {

    suspend fun getCurrencies(): Flow<DataState<String>>
    suspend fun getExchangeRates(): Flow<DataState<ExchangeRatesResponse>>
}
