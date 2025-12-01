package com.example.perpustakaan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.perpustakaan.databinding.ItemStudentBinding
import com.example.perpustakaan.model.Siswa

class StudentAdapter(
    private val onStudentClick: (Siswa) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    private var students = listOf<Siswa>()

    fun updateStudents(newStudents: List<Siswa>) {
        students = newStudents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(students[position])
    }

    override fun getItemCount() = students.size

    inner class StudentViewHolder(
        private val binding: ItemStudentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(siswa: Siswa) {
            binding.apply {
                tvStudentName.text = siswa.name
                tvStudentInfo.text = "${siswa.kelas} • ${siswa.nisn}"

                root.setOnClickListener {
                    onStudentClick(siswa)
                }
            }
        }
    }
}
