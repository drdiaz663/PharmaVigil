package com.lyra.infirmeriestock.data

import com.google.firebase.Timestamp

data class Product(
    val id: String = "",
    val name: String = "",
    val category: String = "Médicament",
    val location: String = Location.ARMOIRE.name,
    val quantity: Int = 0,
    val minStock: Int = 0,
    val expiryDate: Timestamp? = null,
    val lotNumber: String = "",
    val isStupefiant: Boolean = false
)

enum class Location(val displayName: String) {
    ARMOIRE("Armoire principale"),
    BOITE_SECOURS_1("Boîte de secours 1"),
    BOITE_SECOURS_2("Boîte de secours 2"),
    BOITE_SECOURS_3("Boîte de secours 3"),
    COFFRE_FORT("Coffre fort stupéfiants")
}
