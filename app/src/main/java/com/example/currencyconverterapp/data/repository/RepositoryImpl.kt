package com.example.currencyconverterapp.data.repository

import androidx.annotation.WorkerThread
import com.example.currencyconverterapp.data.DataState
import com.example.currencyconverterapp.data.local.repository.LocalRepository
import com.example.currencyconverterapp.data.remote.*
import com.example.currencyconverterapp.data.model.CurrenciesResponse
import com.example.currencyconverterapp.data.model.ExchangeRatesResponse
import com.example.currencyconverterapp.data.model.toDataBaseModel
import com.example.currencyconverterapp.utils.StringUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

/**
 * This is an implementation of [Repository] to handle communication with [ApiService] server.
 */
class RepositoryImpl @Inject constructor(
    private val stringUtils: StringUtils,
    private val apiService: ApiService,
    private val localRepo:LocalRepository
) : Repository {

    @WorkerThread
    override suspend fun getCurrencies(): Flow<DataState<String>> {
        return flow {
            apiService.getCurrencies().apply {
                this.onSuccessSuspend {
                    data?.let {
                        if(it.success) {
                            localRepo.insertCurrencyNames(it.toDataBaseModel())
                        } else {
                            emit(DataState.error<String>(message = stringUtils.somethingWentWrong()))
                        }
                    }?:run{
                        emit(DataState.error<String>(message = stringUtils.somethingWentWrong()))
                    }
                }.onErrorSuspend {
                    emit(DataState.error<String>(message()))
                }.onExceptionSuspend {
                    if (this.exception is IOException) {
                        emit(DataState.error<String>(stringUtils.noNetworkErrorMessage()))
                    } else {
                        emit(DataState.error<String>(stringUtils.somethingWentWrong()))
                    }
                }
            }
        }
    }

    override suspend fun getExchangeRates(): Flow<DataState<ExchangeRatesResponse>> {
        return flow {
            apiService.getExchangeRates().apply {
                this.onSuccessSuspend {
                    data?.let {
                        if (it.success) emit(DataState.success(it))
                        else emit(DataState.error<ExchangeRatesResponse>(stringUtils.somethingWentWrong()))
                    }
                }.onErrorSuspend {
                    emit(DataState.error<ExchangeRatesResponse>(message()))
                }.onExceptionSuspend {
                    if (this.exception is IOException) {
                        emit(DataState.error<ExchangeRatesResponse>(stringUtils.noNetworkErrorMessage()))
                    } else {
                        emit(DataState.error<ExchangeRatesResponse>(stringUtils.somethingWentWrong()))
                    }
                }
            }
        }
    }

}
