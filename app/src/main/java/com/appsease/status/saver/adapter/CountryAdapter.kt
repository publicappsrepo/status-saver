
package com.appsease.status.saver.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.appsease.status.saver.R
import com.appsease.status.saver.interfaces.ICountryCallback
import com.appsease.status.saver.model.Country
import org.koin.core.component.KoinComponent
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

@SuppressLint("NotifyDataSetChanged")
class CountryAdapter(
    private val context: Context,
    countries: List<Country>,
    private val callback: ICountryCallback,
) : RecyclerView.Adapter<CountryAdapter.ViewHolder>(), KoinComponent {

    var countries: List<Country> by Delegates.observable(countries) { _: KProperty<*>, _: List<Country>, _: List<Country> ->
        notifyDataSetChanged()
    }

    var selectedCode: String? by Delegates.observable(null) { _: KProperty<*>, _: String?, _: String? ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater.from(context).inflate(R.layout.item_country, parent, false).let {
            ViewHolder(it)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = countries[position]
        holder.codeView.text = country.getFormattedCode()
        holder.nameView.text = country.displayName
        holder.checkView.isVisible = country.isoCode == selectedCode
    }

    override fun getItemCount(): Int = countries.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal val codeView: TextView = itemView.findViewById(R.id.countryCode)
        internal val nameView: TextView = itemView.findViewById(R.id.countryName)
        internal val checkView: ImageView = itemView.findViewById(R.id.checkIcon)

        private val country: Country?
            get() = countries.getOrNull(layoutPosition)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val country = this.country ?: return
            callback.countryClick(country)
        }
    }
}