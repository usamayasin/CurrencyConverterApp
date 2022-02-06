package com.example.currencyconverterapp.data.model

import com.example.currencyconverterapp.data.local.models.CurrencyNames
import com.google.gson.annotations.SerializedName

data class CurrenciesResponse(
    @SerializedName("currencies")
    var currencies: HashMap<String, String> = HashMap(),
    @SerializedName("success")
    val success: Boolean,
)

fun CurrenciesResponse.toDataBaseModel(): List<CurrencyNames> {
    val exchangeRatelist: MutableList<CurrencyNames> = ArrayList()
    currencies.map { pair ->
        CurrencyNames(
            pair.key,
            pair.value
        )
    }.run {
        exchangeRatelist.addAll(this)
    }
    return exchangeRatelist
}