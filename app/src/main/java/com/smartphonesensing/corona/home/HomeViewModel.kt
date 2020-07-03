package com.smartphonesensing.corona.home

import androidx.lifecycle.ViewModel
import com.smartphonesensing.corona.R

class HomeViewModel : ViewModel() {

    val images = listOf(
        R.drawable.header_basel,
        R.drawable.header_bern,
        R.drawable.header_chur,
        R.drawable.header_geneva,
        R.drawable.header_lausanne,
        R.drawable.header_locarno,
        R.drawable.header_lugano,
        R.drawable.header_luzern,
        R.drawable.header_stgallen,
        R.drawable.header_zurich
    )
}