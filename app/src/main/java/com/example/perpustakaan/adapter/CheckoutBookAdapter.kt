package com.example.perpustakaan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.perpustakaan.R
import com.example.perpustakaan.databinding.ItemCheckoutBookBinding
import com.example.perpustakaan.model.CartItem
import com.example.perpustakaan.network.CartManager

class CheckoutBookAdapter(
    private val onSelectCodesClick: (CartItem) -> Unit
) : RecyclerView.Adapter<CheckoutBookAdapter.CheckoutBookViewHolder>() {

    private var items = listOf<CartItem>()

    fun updateItems(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutBookViewHolder {
        val binding = ItemCheckoutBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CheckoutBookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CheckoutBookViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class CheckoutBookViewHolder(
        private val binding: ItemCheckoutBookBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.apply {
                val buku = cartItem.buku

                tvBookTitle.text = buku.judul
                tvBookAuthor.text = buku.penulis
                tvQuantity.text = "Jumlah: ${cartItem.quantity}"

                val imageUrl = buku.fotoUrl ?: buku.foto
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .centerCrop()
                    .into(ivBookCover)

                // Show selected codes
                val selectedCodes = CartManager.getSelectedKodeBuku(buku.id)
                if (selectedCodes.isNotEmpty()) {
                    layoutSelectedCodes.visibility = View.VISIBLE
                    tvSelectedCodes.text = selectedCodes.joinToString("\n") { it.kodeBuku }
                    
                    // Update button text
                    btnSelectCodes.text = "Ubah Kode Buku (${selectedCodes.size}/${cartItem.quantity})"
                } else {
                    layoutSelectedCodes.visibility = View.GONE
                    btnSelectCodes.text = "Pilih Kode Buku (0/${cartItem.quantity})"
                }

                btnSelectCodes.setOnClickListener {
                    onSelectCodesClick(cartItem)
                }
            }
        }
    }
}
