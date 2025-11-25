
package com.appsease.status.saver.interfaces

import com.appsease.status.saver.model.Country

interface ICountryCallback {
    fun countryClick(country: Country)
}