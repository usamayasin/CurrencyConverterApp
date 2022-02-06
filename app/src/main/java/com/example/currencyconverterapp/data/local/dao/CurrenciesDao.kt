package com.example.currencyconverterapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.currencyconverterapp.data.local.models.CurrencyNames
import com.example.currencyconverterapp.data.local.models.CurrencyRates
import com.example.currencyconverterapp.utils.Constants.TABLE_CURRENCY
import com.example.currencyconverterapp.utils.Constants.TABLE_CURRENCY_RATES
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CurrenciesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCurrencyNames(currencyNames: List<CurrencyNames>)

    @Query("Select *FROM $TABLE_CURRENCY")
    abstract fun getAllCurrencyNames(): Flow<List<CurrencyNames>>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCurrencyRates(currencyRates: List<CurrencyRates>)

    @Query("Select *FROM $TABLE_CURRENCY_RATES")
    abstract fun getAllCurrencyRates(): List<CurrencyRates>?
}