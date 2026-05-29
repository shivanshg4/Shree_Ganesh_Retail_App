package com.example.shreeganesh.ui.base

sealed class BaseState<out T> {
    object Loading : BaseState<Nothing>()
    data class Success<T>(val data: T) : BaseState<T>()
    data class Error(val exception: Throwable) : BaseState<Nothing>()
}
