package com.example.currencyconverterapp.ui.home

sealed class UIState

object LoadingState : UIState()
object ContentState : UIState()
object EmptyState : UIState()
class ErrorState(val message: String) : UIState()
