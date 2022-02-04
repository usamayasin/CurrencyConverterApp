package com.example.currencyconverterapp.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.showSnack(message: String, action: String = "", actionListener: () -> Unit = {}): Snackbar {
    var snackbar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
    if (action != "") {
        snackbar.duration = Snackbar.LENGTH_INDEFINITE
        snackbar.setAction(action) {
            actionListener()
            snackbar.dismiss()
        }
    }
    snackbar.show()
    return snackbar
}

