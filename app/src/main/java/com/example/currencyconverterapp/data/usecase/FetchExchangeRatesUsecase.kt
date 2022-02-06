package com.example.currencyconverterapp.data.usecase

import com.example.currencyconverterapp.data.DataState
import com.example.currencyconverterapp.data.model.Currency
import com.example.currencyconverterapp.data.repository.Repository
import com.example.currencyconverterapp.utils.Constants
import com.example.currencyconverterapp.utils.StringUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class FetchExchangeRatesUsecase @Inject constructor(
    private val repository: Repository,
    private val stringUtils: StringUtils
) {

    @ExperimentalCoroutinesApi
    suspend operator fun invoke(
        source: String,
        amount: Double
    ): Flow<DataState<MutableList<Currency>>> {

        val rates = repository.getExchangeRates()

        return flow {
            rates.collect { response ->
                when (response) {
                    is DataState.Success -> {
                        var convertedList: MutableList<Currency> = ArrayList()
                        var exchangeRatelist: MutableList<Currency> = ArrayList()
                        if (response.data.success) {
                            exchangeRatelist = mapToExchangeRatesList(response.data.quotes)
                            exchangeRatelist.let { listItems ->
                                if (listItems.isNotEmpty()) {
                                    val currencyValue = amount / listItems.filter {
                                        it.currencyName.contains(source)
                                    }.map {
                                        it.exchangedValue
                                    }.first()
                                    convertedList = createConvertedList(listItems, currencyValue)
                                }
                            }
                            emit(DataState.success(convertedList))
                        } else {
                            emit(DataState.error<MutableList<Currency>>(stringUtils.somethingWentWrong()))
                        }
                    }
                    is DataState.Error -> {
                        emit(DataState.error<MutableList<Currency>>(stringUtils.somethingWentWrong()))
                    }
                }
            }
        }
    }



    private fun createConvertedList(
        list: MutableList<Currency>,
        currencyValue: Double
    ): MutableList<Currency> {
        val convertedList: MutableList<Currency> = ArrayList()
        list.forEach {
            val obj = Currency(
                currencyName = if (it.currencyName == Constants.REMOVE_SOURCE_STRING_FOR_USD) {
                    Constants.DEFAULT_SOURCE_CURRENCY
                } else it.currencyName.replace(
                    Constants.DEFAULT_SOURCE_CURRENCY,
                    ""
                ),
                exchangedValue = String.format(
                    "%.3f",
                    it.exchangedValue * currencyValue
                ).toDouble()
            )
            convertedList.add(obj)
        }
        return convertedList
    }

    private fun mapToExchangeRatesList(quotesMap: HashMap<String, Double>): MutableList<Currency> {
        var exchangeRatelist: MutableList<Currency> = ArrayList()
        quotesMap.map { pair ->
            Currency(
                pair.key,
                pair.value
            )
        }.run {
            exchangeRatelist.addAll(this)
        }
        return exchangeRatelist
    }
}
