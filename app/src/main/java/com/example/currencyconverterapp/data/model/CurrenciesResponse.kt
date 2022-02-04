package com.example.currencyconverterapp.data.model

import com.google.gson.annotations.SerializedName

data class CurrenciesResponse(
    @SerializedName("currencies")
    var currencies: HashMap<String, String> = HashMap(),
    @SerializedName("success")
    val success: Boolean,
)