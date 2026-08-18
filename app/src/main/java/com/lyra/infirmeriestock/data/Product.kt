package com.lyra.infirmeriestock.data

import com.google.firebase.Timestamp

data class Product(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val location: String = "",
    val quantity: Int = 0,
    val minStock: Int = 0,
    val expiryDate: Timestamp? = null,
    val lotNumber: String = "",
    val isStupefiant: Boolean = false
)
