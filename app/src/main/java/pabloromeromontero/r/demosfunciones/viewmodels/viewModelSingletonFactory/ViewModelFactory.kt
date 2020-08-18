package com.procter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CentroViewModel::class.java)) {
            val key = "com.procter.viewmodel.CentroViewModel"
            if(hashMapViewModel.containsKey(key)){
                return getViewModel(key) as T
            } else {
                addViewModel(key, CentroViewModel())
                return getViewModel(key) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }


    companion object {
        val hashMapViewModel = HashMap<String, ViewModel>()
        fun addViewModel(key: String, viewModel: ViewModel){
            hashMapViewModel.put(key, viewModel)
        }
        fun getViewModel(key: String): ViewModel? {
            return hashMapViewModel[key]
        }
    }
}