package com.paypay.currencyconverter.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.paypay.currencyconverter.models.ExchangeRates


@Database(
    entities = [ExchangeRates::class],
    version = 1
)
abstract class ExchangeRatesDatabase : RoomDatabase() {

    abstract fun getExchangeRateDao(): ExchangeRatesDao

}