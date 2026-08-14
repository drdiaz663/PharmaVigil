package com.lyra.infirmeriestock.data

import com.google.firebase.firestore.Timestamp

enum class MovementType {
    ENTREE, SORTIE
}

data class StockMovement(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val type: MovementType = MovementType.ENTREE,
    val quantity: Int = 0,
    val location: String = "",
    val date: Timestamp = Timestamp.now(),
    val note: String = ""
)
