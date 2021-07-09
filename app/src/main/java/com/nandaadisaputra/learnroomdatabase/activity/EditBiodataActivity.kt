package com.nandaadisaputra.learnroomdatabase.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.nandaadisaputra.learnroomdatabase.R
import com.nandaadisaputra.learnroomdatabase.database.BioDataModel
import com.nandaadisaputra.learnroomdatabase.database.MyDatabase
import com.nandaadisaputra.learnroomdatabase.databinding.ActivityEditBiodataBinding
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.util.concurrent.Executors

//TODO 35 Membuat class EditBiodataActivity.kt
class EditBiodataActivity : AppCompatActivity() {
    //TODO 36 Lateinit memilki arti bahwa sebuah variabel akan di inisialisasi nanti di dalam onCreate
    // Kita deklarasikan variabel binding dan variabel database, private artinya variabel tersebut
    // hanya akan di akses di class EditBiodataActivity saja.
    private lateinit var binding: ActivityEditBiodataBinding
    private lateinit var database: MyDatabase

    //TODO 37 Kita deklarasikan variabel idBioData dan name
    var idBioData = 0
    var name = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO 38 Kita inisialisasi binding nya
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_biodata)
        //TODO 40 menerima data yang Kita kirim dari MainActivity menggunakan Intent
        idBioData = intent.getIntExtra("id", 0)
        name = intent.getStringExtra("name") ?: ""
        //activity dari binding.activity = this diambil dari name variabel di activity_edit_biodata nya
        binding.activity = this
        //TODO 41  Kita inisialisasi database nya
        database = MyDatabase.getDatabase(this)
        //TODO 42 Kondisi ketika edittext tidak ada inputan ( kosong )
        if (name.isEmpty()) {
            //validasi
            if (validation()) {
                editData(view = null)
            }
        }
    }

    private fun validation(): Boolean {
        if (binding.editUserName.text.toString().trim().isEmpty()) {
            binding.editUserNameTextInputLayout.error = "Username Tidak Boleh Kosong"
            binding.editUserName.requestFocus()
            return false
        } else {
            binding.editUserNameTextInputLayout.isErrorEnabled = false
        }
        return true
    }

    //TODO 43 Ketika Kita klik button Edit Data maka data akan terupdate
    fun editData(view: View?) {
        //TODO 44 Membuat kondisi ketika inputan tidak kosong
        if (name.isNotEmpty()) {
            //TODO 45 mendeklarasikan variabel bioData
            val bioData = BioDataModel(name).apply {
                id = idBioData
            }
            //database tidak bisa diakses langsung di main thread utama
            //TODO 46  Kita perlu Executors agar dapat dieksekusi di tempat yang berbeda diluar EditBiodataActivity
            Executors.newSingleThreadExecutor().execute {
                //TODO 47 membuat proses menyimpan data bioData
                database.bioDataDao().update(bioData)
                //Ketika data berhasil terupdate akan pindah ke halaman MainActivity
                startActivity<MainActivity>()
            }
            toast("data berhasil di update")
        }
    }

    //TODO 48 Ketika Kita klik button Delete Data maka data akan terhapus
    fun deleteData(view: View) {
        val bioData = BioDataModel(name).apply {
            id = idBioData
        }
        //database tidak bisa diakses langsung di main thread utama
        //TODO 49 Kita perlu Executors agar dapat dieksekusi di tempat yang berbeda diluar EditBiodataActivity
        Executors.newSingleThreadExecutor().execute {
            //TODO 50 membuat proses menghapus data bioData
            database.bioDataDao().delete(bioData)
            //Ketika data berhasil terdelete akan pindah ke halaman MainActivity
            startActivity<MainActivity>()
        }
        toast("data berhasil di hapus")
    }
}