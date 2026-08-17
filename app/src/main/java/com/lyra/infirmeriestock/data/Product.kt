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

enum class Location(val displayName: String, val colorHex: String) {
    ARMOIRE("Armoire principale", "#FFFFFF"),
    BOITE_SECOURS_1("Boîte de secours 1", "#2E7D32"),
    BOITE_SECOURS_2("Boîte de secours 2", "#1565C0"),
    BOITE_SECOURS_3("Boîte de secours 3", "#E65100"),
    COFFRE_FORT("Coffre fort stupéfiants", "#C62828")
}

val CATEGORIES = listOf(
    "Médicament",
    "Pansement",
    "Compresse",
    "Désinfectant",
    "Gants",
    "Masque",
    "Lingette",
    "Embout thermomètre",
    "Crème",
    "Pommade",
    "Produit hygiène"
)
