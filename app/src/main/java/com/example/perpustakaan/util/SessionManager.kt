package com.example.perpustakaan.util

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "PerustkaanSession"
    private const val KEY_SISWA_ID = "siswa_id"
    private const val KEY_SISWA_NAME = "siswa_name"
    private const val KEY_SISWA_KELAS = "siswa_kelas"
    private const val KEY_SISWA_NISN = "siswa_nisn"
    
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    fun saveSiswaSession(context: Context, id: Int, name: String, kelas: String, nisn: String) {
        val editor = getPreferences(context).edit()
        editor.putInt(KEY_SISWA_ID, id)
        editor.putString(KEY_SISWA_NAME, name)
        editor.putString(KEY_SISWA_KELAS, kelas)
        editor.putString(KEY_SISWA_NISN, nisn)
        editor.apply()
    }
    
    fun getSiswaId(context: Context): Int {
        return getPreferences(context).getInt(KEY_SISWA_ID, 0)
    }
    
    fun getSiswaName(context: Context): String {
        return getPreferences(context).getString(KEY_SISWA_NAME, "") ?: ""
    }
    
    fun getSiswaKelas(context: Context): String {
        return getPreferences(context).getString(KEY_SISWA_KELAS, "") ?: ""
    }
    
    fun getSiswaNisn(context: Context): String {
        return getPreferences(context).getString(KEY_SISWA_NISN, "") ?: ""
    }
    
    fun hasSiswaSession(context: Context): Boolean {
        return getSiswaId(context) > 0
    }
    
    fun clearSiswaSession(context: Context) {
        val editor = getPreferences(context).edit()
        editor.remove(KEY_SISWA_ID)
        editor.remove(KEY_SISWA_NAME)
        editor.remove(KEY_SISWA_KELAS)
        editor.remove(KEY_SISWA_NISN)
        editor.commit() // Gunakan commit() untuk immediate write, bukan apply()
    }
    
    // Get kelas number for filtering (7A -> 7, 8B -> 8, 9C -> 9)
    fun getSiswaKelasNumber(context: Context): String? {
        val kelas = getSiswaKelas(context)
        if (kelas.isEmpty()) return null
        
        // Extract first digit (7A -> 7, 8B -> 8, 9C -> 9)
        val regex = Regex("^([7-9])")
        return regex.find(kelas)?.value
    }
}
