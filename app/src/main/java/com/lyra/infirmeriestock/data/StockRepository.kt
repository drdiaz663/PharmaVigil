package com.lyra.infirmeriestock.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class StockRepository {
    private val db = FirebaseFirestore.getInstance()
    private val productsRef = db.collection("products")
    private val movementsRef = db.collection("movements")

    suspend fun getProducts(): List<Product> {
        val snapshot = productsRef.get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Product::class.java)?.copy(id = doc.id)
        }
    }

    suspend fun addProduct(product: Product): Result<Unit> = try {
        productsRef.add(product).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateProduct(product: Product): Result<Unit> = try {
        productsRef.document(product.id).set(product).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteProduct(productId: String): Result<Unit> = try {
        productsRef.document(productId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun registerMovement(movement: StockMovement): Result<Unit> = try {
        val productRef = productsRef.document(movement.productId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(productRef)
            val current = snapshot.toObject(Product::class.java)
                ?: throw Exception("Produit introuvable")

            val newQuantity = when (movement.type) {
                MovementType.ENTREE -> current.quantity + movement.quantity
                MovementType.SORTIE -> current.quantity - movement.quantity
            }
            if (newQuantity < 0) throw Exception("Stock insuffisant")

            transaction.update(productRef, "quantity", newQuantity)
            transaction.set(movementsRef.document(), movement)
        }.await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
