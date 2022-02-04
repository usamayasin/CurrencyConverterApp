package com.example.currencyconverterapp.ui.home

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.currencyconverterapp.data.model.CurrenciesResponse
import com.example.currencyconverterapp.databinding.ActivityMainBinding
import com.example.currencyconverterapp.utils.gone
import com.example.currencyconverterapp.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var bi: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bi = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bi.root)

        initListener()
        initObservations()

    }

    private fun initListener() {
        bi.currenciesSpinner.onItemSelectedListener  =  this
    }

    private fun initObservations() {

        val uiStateObserver = Observer<UIState> { uiState ->
            // Update the UI, in this case
            when (uiState) {
                is LoadingState -> {
                    bi.progressBarCurrencies.visible()
                }
                is EmptyState -> {
                    bi.progressBarCurrencies.gone()
                }
                is ContentState -> {
                    bi.progressBarCurrencies.gone()
                }
                is ErrorState -> {
                    bi.progressBarCurrencies.gone()
                        viewModel.retry()
                }
            }
        }
        viewModel.uiStateLiveData.observe(this,uiStateObserver)

        val currenciesObserver = Observer<CurrenciesResponse> { response ->
            // Update the UI, in this case
            response?.let {
                val adapter = ArrayAdapter(
                    this,
                    R.layout.simple_spinner_item, response.currencies.values.toMutableList()
                )
                bi.currenciesSpinner.adapter = adapter
            }
        }
        viewModel.currenciesLiveData.observe(this,currenciesObserver)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        bi.currenciesSpinner.setSelection(pos)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}