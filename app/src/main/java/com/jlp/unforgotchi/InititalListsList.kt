package com.jlp.unforgotchi

import com.jlp.unforgotchi.ListsItemsVM

fun getInitialLists(): MutableList<ListsItemsVM> {
    return arrayListOf(
        ListsItemsVM(R.drawable.ic_baseline_list_alt_24,"Basics"),
        ListsItemsVM(R.drawable.ic_baseline_list_alt_24,"Sports"),
        ListsItemsVM(R.drawable.ic_baseline_list_alt_24,"Work")
    )
}