package com.example.currencyconverterapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverterapp.data.DataState
import com.example.currencyconverterapp.data.local.models.CurrencyNames
import com.example.currencyconverterapp.data.local.models.CurrencyRates
import com.example.currencyconverterapp.data.usecase.FetchCurrenciesFromRemoteUsecase
import com.example.currencyconverterapp.data.usecase.FetchCurrenciesUsecase
import com.example.currencyconverterapp.data.usecase.FetchExchangeRatesUsecase
import com.example.currencyconverterapp.utils.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fetchCurrenciesUsecase: FetchCurrenciesUsecase,
    private val fetchExchangeRatesUsecase: FetchExchangeRatesUsecase,
    private val fetchCurrenciesFromRemoteUsecase: FetchCurrenciesFromRemoteUsecase,
    private val stringUtils: StringUtils
) : ViewModel() {

    private var _uiState = MutableLiveData<UIState>()
    var uiStateLiveData: LiveData<UIState> = _uiState

    private var _currenciesList = MutableLiveData<List<CurrencyNames>>()
    var currenciesLiveData: LiveData<List<CurrencyNames>> = _currenciesList

    private var _exchangeRateUiState = MutableLiveData<UIState>()
    var exchangeRateUiStateLiveData: LiveData<UIState> = _exchangeRateUiState
    private var _exchangeRatesList = MutableLiveData<List<CurrencyRates>>()
    var exchangeRatesLiveData: LiveData<List<CurrencyRates>> = _exchangeRatesList

    init {
        fetchCurrenciesFromNetwork()
        fetchCurrenciesFromLocalDB()
    }

    private fun fetchCurrenciesFromNetwork(){
        viewModelScope.launch(Dispatchers.IO) {
            fetchCurrenciesFromRemoteUsecase.invoke().collect{ dataState ->
                withContext(Dispatchers.Main){
                    if(dataState is DataState.Error){
                        _uiState.postValue(ErrorState(stringUtils.somethingWentWrong()))
                    }
                }
            }
        }
    }

    private fun fetchCurrenciesFromLocalDB() {
        _uiState.postValue(LoadingState)
        viewModelScope.launch(Dispatchers.IO) {
            fetchCurrenciesUsecase.invoke()?.collect { dataState ->
                withContext(Dispatchers.Main) {
                    if( dataState.isNotEmpty() ){
                        _uiState.postValue(ContentState)
                        _currenciesList.postValue(dataState)
                    } else {
                        _uiState.postValue(ErrorState(stringUtils.somethingWentWrong()))
                    }
                }
            }?:run{
                _uiState.postValue(ErrorState(stringUtils.somethingWentWrong()))
            }
        }
    }

    fun fetchExchangeRates(source: String, amount: Double) {
        _exchangeRateUiState.postValue(LoadingState)
        viewModelScope.launch {
            Dispatchers.IO
            fetchExchangeRatesUsecase.invoke(source = source, amount = amount)
                .collect { dataState ->
                    withContext(Dispatchers.Main) {
                        when (dataState) {
                            is DataState.Success -> {
                                _exchangeRateUiState.postValue(ContentState)
                                _exchangeRatesList.postValue(dataState.data)
                            }
                            is DataState.Error -> {
                                _exchangeRateUiState.postValue(ErrorState(dataState.message))
                            }
                        }
                    }
                }
        }
    }
}
