package com.paypay.currencyconverter.repository

import com.paypay.currencyconverter.api.CurrencyLayerAPI
import com.paypay.currencyconverter.db.ExchangeRatesDao
import com.paypay.currencyconverter.models.ExchangeRates
import com.paypay.currencyconverter.pref.PreferenceProvider
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class ExchangeRatesRepository
@Inject constructor(
    private val exchangeRatesDao: ExchangeRatesDao,
    private val currencyLayerAPI: CurrencyLayerAPI,
    private val preferences: PreferenceProvider

) {

    val allExchangeRates = exchangeRatesDao.getAllExchangeRates()

    suspend fun fetchAndCacheExchangeRates() {
        try {
            val listOfSupportedCurrencies = currencyLayerAPI.getListOfSupportedCurrencies()
            val liveExchangeRates = currencyLayerAPI.getLiveExchangeRates()
            if (listOfSupportedCurrencies.isSuccessful && liveExchangeRates.isSuccessful) {
                listOfSupportedCurrencies.body()?.let { supportedCurrencies ->
                    liveExchangeRates.body()?.let { exchangeRates ->

                       //Add the current system time on prefrence
                        val currentTime = System.currentTimeMillis() / 1000L
                        preferences.saveLatestFetchTime(currentTime.toInt())

                        val exchangeRatesList: MutableList<ExchangeRates> = mutableListOf()
                        val currencies = supportedCurrencies.currencies
                        val exchangeRatesQuotes = exchangeRates.quotes
                        val exchangeRatesSource = exchangeRates.source

                        for (currency in currencies) {
                            val exchangeRate = ExchangeRates(
                                currency.key,
                                currency.value,
                                exchangeRatesQuotes.getOrDefault(
                                    "${exchangeRatesSource}${currency.key}",
                                    0.0
                                )
                            )
                            exchangeRatesList.add(exchangeRate)
                        }

                        exchangeRatesDao.upsertExchangeRates(exchangeRatesList)
                    }
                }
            }
        } catch (e: Exception) {
            // Todo: Error handling
        }
    }

}