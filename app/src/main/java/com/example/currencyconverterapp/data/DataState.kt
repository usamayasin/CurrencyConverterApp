package com.example.currencyconverterapp.data

/**
 * A Sealed class to fetch data from server which will be either data or the error.
 */
sealed class DataState<T>(data: T? = null, message: String? = null) {

    data class Success<T>(val data: T?) : DataState<T>(data = data)
    data class Error<T>(val message: String) : DataState<T>(message=message)

    companion object {
        fun <T> success(data: T) = Success<T>(data)
        fun <T> error(message: String) = Error<T>(message)
    }
}
