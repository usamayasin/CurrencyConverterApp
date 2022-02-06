package com.example.currencyconverterapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverterapp.data.local.models.CurrencyRates
import com.example.currencyconverterapp.databinding.ItemCurrencyQuotesBinding
import javax.inject.Inject

class ExchangeRatesAdapter @Inject constructor() :
    RecyclerView.Adapter<ExchangeRatesAdapter.RatesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatesViewHolder {
        val binding = ItemCurrencyQuotesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RatesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RatesViewHolder, position: Int) {
        holder.bind(differ.currentList[position], position)
    }

    override fun getItemCount() = differ.currentList.size


    inner class RatesViewHolder(private val itemBinding: ItemCurrencyQuotesBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(model: CurrencyRates, position: Int) {
            itemBinding.apply {
                data = model
            }
        }
    }

    private val differCallBack  = object : DiffUtil.ItemCallback<CurrencyRates, >() {

        override fun areItemsTheSame(oldItem: CurrencyRates, newItem: CurrencyRates, ): Boolean {
            return  oldItem.currencyName == newItem.currencyName
        }
        override fun areContentsTheSame(oldItem: CurrencyRates, newItem: CurrencyRates, ): Boolean {
            return  oldItem==newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallBack)
}
