package com.paypay.currencyconverter.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.paypay.currencyconverter.R
import com.paypay.currencyconverter.ui.ExchangeRatesViewModel
import com.paypay.currencyconverter.ui.adapters.ExchangeRatesAdapter
import com.paypay.currencyconverter.util.InputValidationUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_exchange_rate_converter.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExchangeRatesConverterFragment : Fragment(R.layout.fragment_exchange_rate_converter) {

    private val viewModel: ExchangeRatesViewModel by viewModels()
    private lateinit var exchangeRatesAdapter: ExchangeRatesAdapter
    private val args: ExchangeRatesConverterFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        setupRecyclerView()

        country_code_tv.setOnClickListener {
            findNavController().navigate(
                R.id.action_exchangeRateConverterFragment_to_selectCurrencyFragment
            )
        }

        input_currency_edit_text.addTextChangedListener { input ->
            MainScope().launch {
                input?.let {
                    viewModel.updateUserCurrency(InputValidationUtil.handleInput(input.toString()))
                }
            }
        }

        viewModel.getUserCurrencyInput()
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { userInputValue ->
                exchangeRatesAdapter.updateUserInputValue(userInputValue)
            })

        viewModel.getProgress().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            showProgressBar(it)
        })


        args.selectedExchangeRate?.let { selectedExchangeRate ->
            exchangeRatesAdapter.updateUserSelectedCurrency(selectedExchangeRate.exchangeRate)

            country_code_tv.text = selectedExchangeRate.countryCode

            input_currency_edit_text.setText(viewModel.getUserCurrencyInput().value?.let {
                viewModel.getUserCurrencyInput().value.toString()
            })

        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.exchange_rate_menu, menu)
        menu.findItem(R.id.refresh)?.setOnMenuItemClickListener {
            viewModel.refreshData()
            true
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupRecyclerView() {
        exchangeRatesAdapter = ExchangeRatesAdapter()
        exchange_rates_rv.apply {
            adapter = exchangeRatesAdapter
            layoutManager = GridLayoutManager(activity, 3)
        }
        displayExchangeRates()
    }

    private fun displayExchangeRates() {
        viewModel.getAllExchangeRates().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            exchangeRatesAdapter.setList(it)
            exchangeRatesAdapter.notifyDataSetChanged()
        })
    }

    private fun showProgressBar(showProgressBar: Boolean) {
        if (showProgressBar) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.INVISIBLE
        }
    }

}