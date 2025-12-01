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
    private val selectedCodes = mutableSetOf<Int>()

    fun updateBookCodes(newCodes: List<KodeBuku>) {
        bookCodes = newCodes
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
        holder.bind(bookCodes[position])
    }

    override fun getItemCount() = bookCodes.size

    inner class BookCodeViewHolder(
        private val binding: ItemBookCodeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(kodeBuku: KodeBuku) {
            binding.apply {
                tvKodeBuku.text = kodeBuku.kodeBuku
                tvStatus.text = kodeBuku.status.replaceFirstChar { it.uppercase() }
                
                val isSelected = selectedCodes.contains(kodeBuku.id)
                checkbox.isChecked = isSelected

                // Disable if max selection reached and this item is not selected
                val canSelect = selectedCodes.size < maxSelection || isSelected
                checkbox.isEnabled = canSelect
                root.isEnabled = canSelect
                root.alpha = if (canSelect) 1.0f else 0.5f

                checkbox.setOnCheckedChangeListener(null)
                checkbox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        if (selectedCodes.size < maxSelection) {
                            selectedCodes.add(kodeBuku.id)
                            notifyDataSetChanged()
                        } else {
                            checkbox.isChecked = false
                        }
                    } else {
                        selectedCodes.remove(kodeBuku.id)
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
