package com.paypay.currencyconverter.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paypay.currencyconverter.models.ExchangeRates
import com.paypay.currencyconverter.pref.PreferenceProvider
import com.paypay.currencyconverter.repository.ExchangeRatesRepository
import com.paypay.currencyconverter.util.Constants.Companion.FETCH_INTERVAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeRatesViewModel @Inject constructor(
    private val exchangeRatesRepository: ExchangeRatesRepository,
    private val preferences: PreferenceProvider
) : ViewModel() {

    private val _allExchangeRates = exchangeRatesRepository.allExchangeRates
    private val _userCurrencyInput = MutableLiveData<Double>()
    private val _progress = MutableLiveData<Boolean>()

    init {
        fetchAndCacheData()
    }

    fun getAllExchangeRates(): LiveData<List<ExchangeRates>> {
        return _allExchangeRates
    }

    fun getUserCurrencyInput(): LiveData<Double> {
        return _userCurrencyInput
    }

    fun updateUserCurrency(number: Double) {
        _userCurrencyInput.value = number
    }

    fun getProgress(): LiveData<Boolean> {
        return _progress
    }

    fun refreshData() {
        fetchAndCacheData()
    }

    private fun fetchAndCacheData() = viewModelScope.launch {
        if (isFetchNeeded()) {
            _progress.postValue(true)
            try {
                exchangeRatesRepository.fetchAndCacheExchangeRates()
              //  val currentTime = System.currentTimeMillis() / 1000L
               // preferences.saveLatestFetchTime(currentTime.toInt())
            } catch (e: Exception) {
                // Todo: Error handling
            }
            _progress.postValue(false)
        }
    }

    private fun isFetchNeeded(): Boolean {
        val lastFetchTime = preferences.getLastFetchTime()
        if (lastFetchTime == 0) { // first fetch
            return true
        }
        val currentTime = System.currentTimeMillis() / 1000L
        if (currentTime - lastFetchTime >= FETCH_INTERVAL) {
            return true
        }
        return false
    }

}