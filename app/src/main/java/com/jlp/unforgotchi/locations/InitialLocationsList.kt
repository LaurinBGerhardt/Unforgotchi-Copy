package com.jlp.unforgotchi.locations

import com.jlp.unforgotchi.R

fun getInitialLocations(): MutableList<LocationItemsVM> {
    return arrayListOf(
        LocationItemsVM(R.drawable.ic_baseline_location_city_24,"Home"),
        LocationItemsVM(R.drawable.ic_baseline_location_city_24,"Work"),
        LocationItemsVM(R.drawable.ic_baseline_location_city_24,"Family")
    )
}