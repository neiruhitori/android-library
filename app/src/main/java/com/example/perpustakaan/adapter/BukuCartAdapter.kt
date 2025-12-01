package com.example.perpustakaan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.perpustakaan.R
import com.example.perpustakaan.databinding.ItemBukuCartBinding
import com.example.perpustakaan.model.Buku
import com.example.perpustakaan.network.CartManager

class BukuCartAdapter(
    private val onItemClick: (Buku) -> Unit,
    private val showPopularBadge: Boolean = false
) : ListAdapter<Buku, BukuCartAdapter.BukuViewHolder>(BukuDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BukuViewHolder {
        val binding = ItemBukuCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BukuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BukuViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    fun updateData(newData: List<Buku>) {
        submitList(newData)
    }

    inner class BukuViewHolder(
        private val binding: ItemBukuCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(buku: Buku) {
            binding.apply {
                tvBukuJudul.text = buku.judul
                tvBukuPenulis.text = buku.penulis
                tvBukuTahun.text = buku.tahunTerbit.toString()
                tvBukuTipe.text = buku.tipe.replaceFirstChar { it.uppercase() }

                // Load image dengan Glide
                val imageUrl = buku.fotoUrl ?: buku.foto
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .centerCrop()
                    .into(ivBukuCover)

                // Set stock badge
                when {
                    buku.stokTersedia <= 0 -> {
                        tvStokBadge.text = "Habis"
                        tvStokBadge.setTextColor(
                            ContextCompat.getColor(itemView.context, android.R.color.white)
                        )
                        tvStokBadge.setBackgroundResource(R.drawable.bg_badge_unavailable)
                    }
                    else -> {
                        tvStokBadge.text = "${buku.stokTersedia} tersisa"
                        tvStokBadge.setTextColor(
                            ContextCompat.getColor(itemView.context, android.R.color.white)
                        )
                        tvStokBadge.setBackgroundResource(R.drawable.bg_badge_limited)
                    }
                }

                // Show popular badge if needed
                ivPopularBadge.visibility = if (showPopularBadge && buku.totalPeminjaman > 0) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

                // Update UI based on cart state
                val isInCart = CartManager.isInCart(buku.id)
                val quantity = CartManager.getQuantity(buku.id)
                
                if (isInCart) {
                    layoutQuantityControl.visibility = View.VISIBLE
                    btnAddToCart.visibility = View.GONE
                    tvQuantity.text = quantity.toString()
                } else {
                    layoutQuantityControl.visibility = View.GONE
                    btnAddToCart.visibility = View.VISIBLE
                }

                // Disable buttons if stock is empty
                val hasStock = buku.stokTersedia > 0
                btnAddToCart.isEnabled = hasStock
                btnIncrease.isEnabled = hasStock && quantity < buku.stokTersedia

                // Add to cart button click
                btnAddToCart.setOnClickListener {
                    if (hasStock) {
                        CartManager.addItem(buku)
                        notifyItemChanged(adapterPosition)
                    }
                }

                // Increase quantity
                btnIncrease.setOnClickListener {
                    if (quantity < buku.stokTersedia) {
                        CartManager.increaseQuantity(buku.id, buku.stokTersedia)
                        tvQuantity.text = CartManager.getQuantity(buku.id).toString()
                        btnIncrease.isEnabled = CartManager.getQuantity(buku.id) < buku.stokTersedia
                    }
                }

                // Decrease quantity
                btnDecrease.setOnClickListener {
                    CartManager.decreaseQuantity(buku.id)
                    val newQuantity = CartManager.getQuantity(buku.id)
                    if (newQuantity == 0) {
                        notifyItemChanged(adapterPosition)
                    } else {
                        tvQuantity.text = newQuantity.toString()
                        btnIncrease.isEnabled = newQuantity < buku.stokTersedia
                    }
                }

                // Item click for details
                root.setOnClickListener {
                    onItemClick(buku)
                }
            }
        }
    }

    class BukuDiffCallback : DiffUtil.ItemCallback<Buku>() {
        override fun areItemsTheSame(oldItem: Buku, newItem: Buku): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Buku, newItem: Buku): Boolean {
            return oldItem == newItem
        }
    }
}
