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
import com.example.perpustakaan.databinding.ItemBukuGridBinding
import com.example.perpustakaan.model.Buku

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
