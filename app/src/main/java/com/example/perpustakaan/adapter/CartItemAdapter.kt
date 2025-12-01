package com.example.perpustakaan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.perpustakaan.R
import com.example.perpustakaan.databinding.ItemCartBinding
import com.example.perpustakaan.model.CartItem
import com.example.perpustakaan.network.CartManager

class CartItemAdapter : RecyclerView.Adapter<CartItemAdapter.CartViewHolder>() {

    private var items = listOf<CartItem>()

    fun updateItems(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.apply {
                val buku = cartItem.buku

                // Set book info
                tvBookTitle.text = buku.judul
                tvBookAuthor.text = buku.penulis
                tvBookType.text = buku.tipe.replaceFirstChar { it.uppercase() }
                tvStock.text = "${buku.stokTersedia} tersedia"
                tvQuantity.text = cartItem.quantity.toString()

                // Load image
                val imageUrl = buku.fotoUrl ?: buku.foto
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .centerCrop()
                    .into(ivBookCover)

                // Enable/disable increase button based on stock
                btnIncrease.isEnabled = cartItem.quantity < buku.stokTersedia

                // Button listeners
                btnIncrease.setOnClickListener {
                    if (cartItem.quantity < buku.stokTersedia) {
                        CartManager.increaseQuantity(buku.id, buku.stokTersedia)
                        tvQuantity.text = CartManager.getQuantity(buku.id).toString()
                        btnIncrease.isEnabled = CartManager.getQuantity(buku.id) < buku.stokTersedia
                    }
                }

                btnDecrease.setOnClickListener {
                    CartManager.decreaseQuantity(buku.id)
                    val newQuantity = CartManager.getQuantity(buku.id)
                    if (newQuantity == 0) {
                        // Item will be removed from cart, adapter will update
                    } else {
                        tvQuantity.text = newQuantity.toString()
                        btnIncrease.isEnabled = newQuantity < buku.stokTersedia
                    }
                }

                btnRemove.setOnClickListener {
                    CartManager.removeItem(buku.id)
                }
            }
        }
    }
}
