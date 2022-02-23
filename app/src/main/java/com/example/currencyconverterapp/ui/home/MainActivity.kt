package com.example.currencyconverterapp.ui.home

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.currencyconverterapp.R
import com.example.currencyconverterapp.adapters.ExchangeRatesAdapter
import com.example.currencyconverterapp.data.local.models.CurrencyNamesEntity
import com.example.currencyconverterapp.data.local.models.CurrencyRatesEntity
import com.example.currencyconverterapp.databinding.ActivityMainBinding
import com.example.currencyconverterapp.utils.Constants
import com.example.currencyconverterapp.utils.gone
import com.example.currencyconverterapp.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var bi: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var currencyEntities: MutableList<CurrencyNamesEntity> = ArrayList<CurrencyNamesEntity>()
    private var selectedCurrency: String = Constants.DEFAULT_SOURCE_CURRENCY
    private lateinit var exchangeRatesAdapter: ExchangeRatesAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bi = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bi.root)

        initListener()
        initObservations()
    }

    private fun initListener() {

        Glide.with(this)
            .load(R.drawable.loader_icon)
            .into(bi.ivConversionIcon);

        bi.currenciesSpinner.onItemSelectedListener = this
        exchangeRatesAdapter = ExchangeRatesAdapter()
        bi.rvConvertedCurrencies.adapter = exchangeRatesAdapter
        bi.btnConvert.setOnClickListener {
            if (checkValidation()) {
                hideKeyboard()
                viewModel.fetchExchangeRates(
                    selectedCurrency,
                    bi.etAmount.text.toString().toDouble()
                )
            }
        }
    }

    private fun initObservations() {

        val uiStateObserver = Observer<UIState> { uiState ->
            // Update the UI, in this case
            when (uiState) {
                is LoadingState -> {
                    bi.progressBarCurrencies.visible()
                    bi.errorMessageLayout.root.gone()
                    bi.ivConversionIcon.visible()
                }
                is EmptyState -> {
                    bi.progressBarCurrencies.gone()
                    bi.ivConversionIcon.gone()
                    bi.errorMessageLayout.root.visible()
                }
                is ContentState -> {
                    bi.progressBarCurrencies.gone()
                    bi.ivConversionIcon.visible()
                    bi.errorMessageLayout.root.gone()
                }
                is ErrorState -> {
                    bi.progressBarCurrencies.gone()
                    bi.ivConversionIcon.gone()
                    bi.errorMessageLayout.root.visible()
                }
            }
        }
        viewModel.uiStateLiveData.observe(this,uiStateObserver)

        val currenciesObserver = Observer<List<CurrencyNamesEntity>> { response ->
            // Update the UI, in this case
            response?.let {
                currencyEntities = response.toMutableList()
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    response.map { it.currencyCountryName }.sorted()
                )
                bi.currenciesSpinner.adapter = adapter
            }
        }
        viewModel.currenciesLiveData.observe(this, currenciesObserver)

        val exchangeRatesUiStateObserver = Observer<UIState> { uiState ->
            // Update the UI, in this case
            when (uiState) {
                is LoadingState -> {
                    bi.ivConversionIcon.visible()
                    bi.rvConvertedCurrencies.gone()
                    bi.errorMessageLayout.root.gone()
                }
                is EmptyState -> {
                    bi.ivConversionIcon.gone()
                    bi.rvConvertedCurrencies.gone()
                    bi.errorMessageLayout.root.visible()
                }
                is ContentState -> {
                    bi.ivConversionIcon.gone()
                    bi.errorMessageLayout.root.gone()
                    bi.rvConvertedCurrencies.visible()
                }
                is ErrorState -> {
                    bi.ivConversionIcon.gone()
                    bi.rvConvertedCurrencies.gone()
                    bi.errorMessageLayout.root.visible()
                }
            }
        }
        viewModel.exchangeRateUiStateLiveData.observe(this, exchangeRatesUiStateObserver)

        val exchangeRatesObserver = Observer<List<CurrencyRatesEntity>> { response ->
            // Update the UI, in this case
            response?.let {
                if (response.isNotEmpty()) {
                    exchangeRatesAdapter.differ.submitList(response)
                }
            }
        }
        viewModel.exchangeRatesEntityLiveData.observe(this, exchangeRatesObserver)
    }

    private fun checkValidation(): Boolean {
        bi.etAmount.error = null
        if (bi.etAmount.text.isNullOrEmpty() ||
            bi.etAmount.text.isNullOrBlank() ||
            bi.etAmount.text?.trim().toString() == ".") {

            bi.etAmount.error = getString(R.string.enter_amount_error)
            return false
        }
        return true
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        bi.currenciesSpinner.setSelection(pos)
        selectedCurrency = Constants.DEFAULT_SOURCE_CURRENCY + currencyEntities.single() {
            it.currencyCountryName == bi.currenciesSpinner.selectedItem.toString()
        }.currencyName
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}