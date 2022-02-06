package com.example.currencyconverterapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverterapp.data.DataState
import com.example.currencyconverterapp.data.local.models.CurrencyNamesEntity
import com.example.currencyconverterapp.data.local.models.CurrencyRatesEntity
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
    private val stringUtils: StringUtils
) : ViewModel() {

    private var _uiState = MutableLiveData<UIState>()
    var uiStateLiveData: LiveData<UIState> = _uiState

    private var _currenciesList = MutableLiveData<List<CurrencyNamesEntity>>()
    var currenciesLiveData: LiveData<List<CurrencyNamesEntity>> = _currenciesList

    private var _exchangeRateUiState = MutableLiveData<UIState>()
    var exchangeRateUiStateLiveData: LiveData<UIState> = _exchangeRateUiState
    private var _exchangeRatesList = MutableLiveData<List<CurrencyRatesEntity>>()
    var exchangeRatesEntityLiveData: LiveData<List<CurrencyRatesEntity>> = _exchangeRatesList

    init {
        fetchCurrenciesFromLocalDB()
    }

    private fun fetchCurrenciesFromLocalDB() {
        _uiState.postValue(LoadingState)
        viewModelScope.launch(Dispatchers.IO) {
            fetchCurrenciesUsecase.invoke().collect { dataState ->
                withContext(Dispatchers.Main) {
                    when(dataState){
                        is DataState.Success -> {
                            _uiState.postValue(ContentState)
                            _currenciesList.postValue(dataState.data)
                        }
                        is DataState.Error -> {
                            _uiState.postValue(ErrorState(stringUtils.somethingWentWrong()))
                        }
                    }
                }
            }
        }
    }

    fun fetchExchangeRates(source: String, amount: Double) {
        _exchangeRateUiState.postValue(LoadingState)
        viewModelScope.launch(Dispatchers.IO) {
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
