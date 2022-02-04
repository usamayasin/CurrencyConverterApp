package com.example.currencyconverterapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverterapp.data.DataState
import com.example.currencyconverterapp.data.model.CurrenciesResponse
import com.example.currencyconverterapp.data.usecase.FetchCurrenciesUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fetchCurrenciesUsecase: FetchCurrenciesUsecase,
) : ViewModel() {

    private var _uiState = MutableLiveData<UIState>()
    var uiStateLiveData: LiveData<UIState> = _uiState

    private var _currenciesList = MutableLiveData<CurrenciesResponse>()
    var currenciesLiveData: LiveData<CurrenciesResponse> = _currenciesList

    init {
        fetchCurrencies()
    }

    fun retry() {
        fetchCurrencies()
    }

    private fun fetchCurrencies() {
        _uiState.postValue(LoadingState)
        viewModelScope.launch {Dispatchers.IO
            fetchCurrenciesUsecase.invoke().collect { dataState ->
                withContext(Dispatchers.Main){
                    when(dataState){
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
}
