package com.example.perpustakaan.network

import com.example.perpustakaan.model.Buku
import com.example.perpustakaan.model.CartItem
import com.example.perpustakaan.model.KodeBuku

object CartManager {
    private val cartItems = mutableListOf<CartItem>()
    private val listeners = mutableListOf<CartUpdateListener>()
    
    interface CartUpdateListener {
        fun onCartUpdated()
    }
    
    fun addListener(listener: CartUpdateListener) {
        listeners.add(listener)
    }
    
    fun removeListener(listener: CartUpdateListener) {
        listeners.remove(listener)
    }
    
    private fun notifyListeners() {
        listeners.forEach { it.onCartUpdated() }
    }
    
    fun addItem(buku: Buku) {
        val existingItem = cartItems.find { it.buku.id == buku.id }
        if (existingItem == null) {
            cartItems.add(CartItem(buku, quantity = 1))
        } else {
            existingItem.quantity++
        }
        notifyListeners()
    }
    
    fun removeItem(bukuId: Int) {
        cartItems.removeAll { it.buku.id == bukuId }
        notifyListeners()
    }
    
    fun increaseQuantity(bukuId: Int, maxQuantity: Int) {
        val item = cartItems.find { it.buku.id == bukuId }
        if (item != null && item.quantity < maxQuantity) {
            item.quantity++
            notifyListeners()
        }
    }
    
    fun decreaseQuantity(bukuId: Int) {
        val item = cartItems.find { it.buku.id == bukuId }
        if (item != null) {
            if (item.quantity > 1) {
                item.quantity--
            } else {
                cartItems.remove(item)
            }
            notifyListeners()
        }
    }
    
    fun setSelectedKodeBuku(bukuId: Int, kodeBukuList: List<KodeBuku>) {
        val item = cartItems.find { it.buku.id == bukuId }
        item?.selectedKodeBuku = kodeBukuList.toMutableList()
        notifyListeners()
    }
    
    fun getSelectedKodeBuku(bukuId: Int): List<KodeBuku> {
        return cartItems.find { it.buku.id == bukuId }?.selectedKodeBuku ?: emptyList()
    }
    
    fun getCartItems(): List<CartItem> {
        return cartItems.toList()
    }
    
    fun getTotalItems(): Int {
        return cartItems.sumOf { it.quantity }
    }
    
    fun getTotalBooksCount(): Int {
        return cartItems.size
    }
    
    fun getQuantity(bukuId: Int): Int {
        return cartItems.find { it.buku.id == bukuId }?.quantity ?: 0
    }
    
    fun isInCart(bukuId: Int): Boolean {
        return cartItems.any { it.buku.id == bukuId }
    }
    
    fun clearCart() {
        cartItems.clear()
        notifyListeners()
    }
}
