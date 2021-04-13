package com.paypay.currencyconverter.api

import com.paypay.currencyconverter.BuildConfig
import com.paypay.currencyconverter.models.LiveExchangeRatesResponse
import com.paypay.currencyconverter.models.SupportedCurrenciesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyLayerAPI {

    @GET("list")
    suspend fun getListOfSupportedCurrencies(
        @Query("access_key")
        apiKey: String = BuildConfig.API_KEY
    ): Response<SupportedCurrenciesResponse>

    @GET("live")
    suspend fun getLiveExchangeRates(
        @Query("access_key")
        apiKey: String = BuildConfig.API_KEY
    ): Response<LiveExchangeRatesResponse>

}