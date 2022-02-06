package com.example.currencyconverterapp.data.local.repository

import com.example.currencyconverterapp.data.local.dao.CurrenciesDao
import com.example.currencyconverterapp.data.local.models.CurrencyNames
import com.example.currencyconverterapp.data.local.models.CurrencyRates
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalRepository @Inject constructor(var currenciesDao: CurrenciesDao) {

    val storeCurrencies = currenciesDao.getAllCurrencyNames()

    fun insertCurrencyNames(currencyNames: List<CurrencyNames>) {
        currenciesDao.insertCurrencyNames(currencyNames)
    }

    fun getAllCurrencyNames(): Flow<List<CurrencyNames>>? {
        return currenciesDao.getAllCurrencyNames()
    }

    fun insertCurrencyRates(currencyRates: List<CurrencyRates>) {
        currenciesDao.insertCurrencyRates(currencyRates)
    }

    fun getAllCurrencyRates(): List<CurrencyRates>? {
        return currenciesDao.getAllCurrencyRates()
    }
}