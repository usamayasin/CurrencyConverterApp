package com.example.currencyconverterapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverterapp.data.DataState
import com.example.currencyconverterapp.data.model.CurrenciesResponse
import com.example.currencyconverterapp.data.model.Currency
import com.example.currencyconverterapp.data.usecase.FetchCurrenciesUsecase
import com.example.currencyconverterapp.data.usecase.FetchExchangeRatesUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fetchCurrenciesUsecase: FetchCurrenciesUsecase,
    private val fetchExchangeRatesUsecase: FetchExchangeRatesUsecase
) : ViewModel() {

    private var _uiState = MutableLiveData<UIState>()
    var uiStateLiveData: LiveData<UIState> = _uiState

    private var _currenciesList = MutableLiveData<CurrenciesResponse>()
    var currenciesLiveData: LiveData<CurrenciesResponse> = _currenciesList

    private var _exchangeRateUiState = MutableLiveData<UIState>()
    var exchangeRateUiStateLiveData: LiveData<UIState> = _exchangeRateUiState
    private var _exchangeRatesList = MutableLiveData<List<Currency>>()
    var exchangeRatesLiveData: LiveData<List<Currency>> = _exchangeRatesList

    init {
        fetchCurrencies()
    }

    private fun fetchCurrencies() {
        _uiState.postValue(LoadingState)
        viewModelScope.launch {
            Dispatchers.IO
            fetchCurrenciesUsecase.invoke().collect { dataState ->
                withContext(Dispatchers.Main) {
                    when (dataState) {
                        is DataState.Success -> {
                            _uiState.postValue(ContentState)
                            _currenciesList.postValue(dataState.data)
                        }
                        is DataState.Error -> {
                            _uiState.postValue(ErrorState(dataState.message))
                        }
                    }
                }
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
