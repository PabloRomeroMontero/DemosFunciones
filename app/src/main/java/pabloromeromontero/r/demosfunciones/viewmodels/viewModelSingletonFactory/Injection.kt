package com.procter.viewmodel
import android.content.Context

object Injection {
    fun provideViewModelFactory(context: Context): ViewModelFactory { return ViewModelFactory()
    }
}