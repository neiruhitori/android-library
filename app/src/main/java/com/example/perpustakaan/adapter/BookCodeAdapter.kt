package com.example.perpustakaan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.perpustakaan.databinding.ItemBookCodeBinding
import com.example.perpustakaan.model.KodeBuku

class BookCodeAdapter(
    private val maxSelection: Int
) : RecyclerView.Adapter<BookCodeAdapter.BookCodeViewHolder>() {

    private var bookCodes = listOf<KodeBuku>()
    private var filteredBookCodes = listOf<KodeBuku>()
    private val selectedCodes = mutableSetOf<Int>()

    fun updateBookCodes(newCodes: List<KodeBuku>) {
        android.util.Log.d("BookCodeAdapter", "Updating book codes: ${newCodes.size} items")
        bookCodes = newCodes
        filteredBookCodes = newCodes
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredBookCodes = if (query.isEmpty()) {
            bookCodes
        } else {
            bookCodes.filter { 
                it.kodeBuku.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    fun getSelectedCodes(): List<KodeBuku> {
        return bookCodes.filter { selectedCodes.contains(it.id) }
    }

    fun setSelectedCodes(codes: List<KodeBuku>) {
        selectedCodes.clear()
        selectedCodes.addAll(codes.map { it.id })
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookCodeViewHolder {
        val binding = ItemBookCodeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookCodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookCodeViewHolder, position: Int) {
        holder.bind(filteredBookCodes[position])
    }

    override fun getItemCount() = filteredBookCodes.size

    inner class BookCodeViewHolder(
        private val binding: ItemBookCodeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(kodeBuku: KodeBuku) {
            binding.apply {
                tvKodeBuku.text = kodeBuku.kodeBuku
                tvStatus.text = kodeBuku.status.replaceFirstChar { it.uppercase() }
                
                val isSelected = selectedCodes.contains(kodeBuku.id)
                
                android.util.Log.d("BookCodeAdapter", "Binding ${kodeBuku.kodeBuku}, selected: $isSelected, position: $adapterPosition")
                
                // Update checkbox state tanpa trigger listener
                checkbox.setOnCheckedChangeListener(null)
                checkbox.isChecked = isSelected
                checkbox.visibility = android.view.View.VISIBLE // Pastikan checkbox visible

                // Disable if max selection reached and this item is not selected
                val canSelect = selectedCodes.size < maxSelection || isSelected
                checkbox.isEnabled = canSelect
                root.isEnabled = canSelect
                root.alpha = if (canSelect) 1.0f else 0.5f

                // Set listener untuk checkbox
                checkbox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        if (selectedCodes.size < maxSelection) {
                            selectedCodes.add(kodeBuku.id)
                            // Hanya notify item yang berubah, bukan semua
                            notifyItemChanged(adapterPosition)
                            // Update items yang mungkin terpengaruh (enable/disable)
                            if (selectedCodes.size == maxSelection) {
                                notifyDataSetChanged()
                            }
                        } else {
                            checkbox.isChecked = false
                        }
                    } else {
                        selectedCodes.remove(kodeBuku.id)
                        // Notify perubahan
                        notifyItemChanged(adapterPosition)
                        // Update semua item karena ada yang bisa di-enable lagi
                        notifyDataSetChanged()
                    }
                }

                root.setOnClickListener {
                    if (canSelect) {
                        checkbox.isChecked = !checkbox.isChecked
                    }
                }
            }
        }
    }
}
