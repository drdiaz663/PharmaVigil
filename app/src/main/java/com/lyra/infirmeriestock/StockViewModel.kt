package com.lyra.infirmeriestock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.lyra.infirmeriestock.data.Location
import com.lyra.infirmeriestock.data.MovementType
import com.lyra.infirmeriestock.data.Product
import com.lyra.infirmeriestock.data.StockMovement
import com.lyra.infirmeriestock.data.StockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StockViewModel : ViewModel() {
    private val repo = StockRepository()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun loadProducts() {
        viewModelScope.launch {
            _products.value = repo.getProducts()
        }
    }

    fun addProduct(
        name: String,
        category: String,
        location: String,
        quantity: Int,
        minStock: Int,
        expiryDate: Timestamp?,
        lotNumber: String,
        isStupefiant: Boolean
    ) {
        viewModelScope.launch {
            val product = Product(
                name = name,
                category = category,
                location = location,
                quantity = quantity,
                minStock = minStock,
                expiryDate = expiryDate,
                lotNumber = lotNumber,
                isStupefiant = isStupefiant
            )
            repo.addProduct(product)
                .onSuccess { loadProducts() }
                .onFailure { _message.value = it.message }
        }
    }

    fun registerMovement(
        product: Product,
        type: MovementType,
        quantity: Int,
        note: String
    ) {
        viewModelScope.launch {
            val movement = StockMovement(
                productId = product.id,
                productName = product.name,
                type = type,
                quantity = quantity,
                location = product.location,
                note = note
            )
            repo.registerMovement(movement)
                .onSuccess { loadProducts() }
                .onFailure { _message.value = it.message }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
