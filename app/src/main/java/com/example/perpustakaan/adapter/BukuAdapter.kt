package com.example.perpustakaan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.perpustakaan.R
import com.example.perpustakaan.databinding.ItemBukuGridBinding
import com.example.perpustakaan.model.Buku
import com.example.perpustakaan.network.CartManager

class BukuAdapter(
    private val onItemClick: (Buku) -> Unit,
    private val showPopularBadge: Boolean = false
) : ListAdapter<Buku, BukuAdapter.BukuViewHolder>(BukuDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BukuViewHolder {
        val binding = ItemBukuGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BukuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BukuViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    // Method untuk update data (digunakan di BukuKategoriActivity)
    fun updateData(newData: List<Buku>) {
        submitList(newData)
    }

    inner class BukuViewHolder(
        private val binding: ItemBukuGridBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(buku: Buku) {
            binding.apply {
                tvBukuJudul.text = buku.judul
                tvBukuPenulis.text = buku.penulis
                tvBukuTahun.text = buku.tahunTerbit.toString()
                tvBukuTipe.text = buku.tipe.replaceFirstChar { it.uppercase() }
                
                // Tampilkan kelas untuk buku tahunan
                if (buku.tipe == "tahunan" && !buku.kelas.isNullOrEmpty()) {
                    tvBukuKelas.text = "Kelas ${buku.kelas}"
                    tvBukuKelas.visibility = View.VISIBLE
                } else {
                    tvBukuKelas.visibility = View.GONE
                }

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

                // Get current quantity from cart
                val currentQuantity = CartManager.getQuantity(buku.id)
                updateQuantityUI(currentQuantity)

                // Button Add - Show ketika quantity = 0
                btnAdd.setOnClickListener {
                    if (buku.stokTersedia <= 0) {
                        Toast.makeText(itemView.context, "Stok habis", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    
                    CartManager.addItem(buku)
                    updateQuantityUI(1)
                    Toast.makeText(itemView.context, "${buku.judul} ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
                }

                // Button Increase
                btnIncrease.setOnClickListener {
                    val currentQty = CartManager.getQuantity(buku.id)
                    val newQuantity = currentQty + 1
                    if (newQuantity > buku.stokTersedia) {
                        Toast.makeText(
                            itemView.context,
                            "Maksimal ${buku.stokTersedia} buku tersedia",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                    
                    CartManager.increaseQuantity(buku.id, buku.stokTersedia)
                    updateQuantityUI(newQuantity)
                }

                // Button Decrease
                btnDecrease.setOnClickListener {
                    CartManager.decreaseQuantity(buku.id)
                    val newQuantity = CartManager.getQuantity(buku.id)
                    updateQuantityUI(newQuantity)
                    
                    if (newQuantity == 0) {
                        Toast.makeText(itemView.context, "${buku.judul} dihapus dari keranjang", Toast.LENGTH_SHORT).show()
                    }
                }

                // Click on card -> detail
                root.setOnClickListener {
                    onItemClick(buku)
                }
            }
        }

        private fun ItemBukuGridBinding.updateQuantityUI(quantity: Int) {
            if (quantity > 0) {
                btnAdd.visibility = View.GONE
                layoutQuantityControls.visibility = View.VISIBLE
                tvQuantity.text = quantity.toString()
            } else {
                btnAdd.visibility = View.VISIBLE
                layoutQuantityControls.visibility = View.GONE
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
