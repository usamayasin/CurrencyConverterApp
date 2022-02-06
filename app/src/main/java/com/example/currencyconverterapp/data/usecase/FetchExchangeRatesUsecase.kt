package com.example.currencyconverterapp.data.usecase

import com.example.currencyconverterapp.data.DataState
import com.example.currencyconverterapp.data.local.models.CurrencyRates
import com.example.currencyconverterapp.data.local.repository.LocalRepository
import com.example.currencyconverterapp.data.repository.Repository
import com.example.currencyconverterapp.utils.Constants
import com.example.currencyconverterapp.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class FetchExchangeRatesUsecase @Inject constructor(
    private val repository: Repository,
    private val stringUtils: StringUtils,
    private val localRepo: LocalRepository
) {

    @ExperimentalCoroutinesApi
    suspend operator fun invoke(
        source: String,
        amount: Double
    ): Flow<DataState<MutableList<CurrencyRates>>> {
        return flow {
            val responseFromDatabase = localRepo.getAllCurrencyRates()
            if ( responseFromDatabase!!.isNullOrEmpty().not()){
                var convertedList: MutableList<CurrencyRates> = ArrayList()
                val currencyValue = getCurrencyValue(responseFromDatabase.toMutableList(),amount,source)
                convertedList = createConvertedList(responseFromDatabase.toMutableList(), currencyValue)
                emit(DataState.success(convertedList))
            } else {
                val rates = repository.getExchangeRates()
                rates.collect { response ->
                    when (response) {
                        is DataState.Success -> {
                            var convertedList: MutableList<CurrencyRates> = ArrayList()
                            var exchangeRatelist: MutableList<CurrencyRates> = ArrayList()
                            if (response.data!!.success) {
                                exchangeRatelist = mapToExchangeRatesList(response.data.quotes)
                                exchangeRatelist?.let { listItems ->
                                    if (listItems.isNotEmpty()) {
                                        val currencyValue = getCurrencyValue(listItems,amount,source)
                                        convertedList = createConvertedList(listItems, currencyValue)
                                    }
                                }
                                localRepo.insertCurrencyRates(exchangeRatelist)
                                emit(DataState.success(convertedList))
                            } else {
                                emit(DataState.error<MutableList<CurrencyRates>>(stringUtils.somethingWentWrong()))
                            }
                        }
                        is DataState.Error -> {
                            emit(DataState.error<MutableList<CurrencyRates>>(stringUtils.somethingWentWrong()))
                        }
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }


    private fun createConvertedList(
        list: MutableList<CurrencyRates>,
        currencyValue: Double
    ): MutableList<CurrencyRates> {
        val convertedList: MutableList<CurrencyRates> = ArrayList()
        list.forEach {
            val obj = CurrencyRates(
                currencyName = if (it.currencyName == Constants.REMOVE_SOURCE_STRING_FOR_USD) {
                    Constants.DEFAULT_SOURCE_CURRENCY
                } else it.currencyName.replace(
                    Constants.DEFAULT_SOURCE_CURRENCY,
                    ""
                ),
                currencyExchangeValue = String.format(
                    "%.3f",
                    it.currencyExchangeValue * currencyValue
                ).toDouble()
            )
            convertedList.add(obj)
        }
        return convertedList
    }

    private fun mapToExchangeRatesList(quotesMap: HashMap<String, Double>): MutableList<CurrencyRates> {
        var exchangeRatelist: MutableList<CurrencyRates> = ArrayList()
        quotesMap.map { pair ->
            CurrencyRates(
                pair.key,
                pair.value
            )
        }.run {
            exchangeRatelist.addAll(this)
        }
        return exchangeRatelist
    }

    private fun getCurrencyValue(list: MutableList<CurrencyRates>, amount: Double, source: String) =
        amount / list.filter {
            it.currencyName.contains(source)
        }.map {
            it.currencyExchangeValue
        }.first()

}
