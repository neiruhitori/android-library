package com.example.perpustakaan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.perpustakaan.R
import com.example.perpustakaan.databinding.ItemBukuBinding
import com.example.perpustakaan.model.Buku

class BukuAdapter(
    private val onItemClick: (Buku) -> Unit
) : ListAdapter<Buku, BukuAdapter.BukuViewHolder>(BukuDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BukuViewHolder {
        val binding = ItemBukuBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BukuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BukuViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BukuViewHolder(
        private val binding: ItemBukuBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(buku: Buku) {
            binding.apply {
                tvBukuJudul.text = buku.judul
                tvBukuPenulis.text = buku.penulis
                tvBukuTahun.text = buku.tahunTerbit.toString()
                tvBukuTipe.text = buku.tipe.capitalize()
                tvBukuDescription.text = buku.description

                // Load image dengan Glide - gunakan foto_url dari API
                val imageUrl = buku.fotoUrl ?: buku.foto
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .into(ivBukuCover)

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
