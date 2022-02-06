package com.example.currencyconverterapp.data.usecase

import com.example.currencyconverterapp.data.DataState
import com.example.currencyconverterapp.data.local.models.CurrencyNamesEntity
import com.example.currencyconverterapp.data.local.repository.LocalRepository
import com.example.currencyconverterapp.data.model.toDataBaseModel
import com.example.currencyconverterapp.data.repository.Repository
import com.example.currencyconverterapp.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FetchCurrenciesUsecase @Inject constructor(
    private val repository: Repository,
    private val stringUtils: StringUtils,
    private val localRepo: LocalRepository
) {

    @ExperimentalCoroutinesApi
    suspend operator fun invoke(): Flow<DataState<List<CurrencyNamesEntity>>> {
        return flow {
            val responseFromLocalDatabase = localRepo.getAllCurrencyNames()
            if (responseFromLocalDatabase.isNullOrEmpty().not()) {
                emit(DataState.success(responseFromLocalDatabase))
            } else {
                val rates = repository.getCurrencies()
                rates.collect { response ->
                    when (response) {
                        is DataState.Success -> {
                            val currenciesList =
                                response.data?.toDataBaseModel() ?: emptyList<CurrencyNamesEntity>()
                            localRepo.insertCurrencyNames(currenciesList)
                            emit(DataState.success(currenciesList))
                        }
                        is DataState.Error -> {
                            emit(DataState.error<List<CurrencyNamesEntity>>(stringUtils.somethingWentWrong()))
                        }
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }
}
