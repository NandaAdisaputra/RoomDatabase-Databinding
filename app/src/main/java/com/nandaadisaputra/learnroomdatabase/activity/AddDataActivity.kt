package com.nandaadisaputra.learnroomdatabase.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.nandaadisaputra.learnroomdatabase.R
import com.nandaadisaputra.learnroomdatabase.database.BioDataModel
import com.nandaadisaputra.learnroomdatabase.database.MyDatabase
import com.nandaadisaputra.learnroomdatabase.databinding.ActivityAddDataBinding
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.util.concurrent.Executors

//TODO 23 Membuat class AddDataActivity.kt
class AddDataActivity : AppCompatActivity() {
    //TODO 25 Lateinit memilki arti bahwa sebuah variabel akan di inisialisasi nanti di dalam onCreate
    // Kita deklarasikan variabel binding dan variabel database, private artinya variabel tersebut
    // hanya akan di akses di class AddDataActivity saja.
    private lateinit var binding: ActivityAddDataBinding
    private lateinit var database: MyDatabase
    //TODO 26 Kita deklarasikan variabel name
    var name = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO 27 Kita inisialisasi binding nya
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_data)
        //activity dari binding.activity = this diambil dari name variabel di activity_add_data nya
        binding.activity = this
        //TODO 28 Kita inisialisasi database nya
        database = MyDatabase.getDatabase(this)
        //TODO 29 Kondisi ketika edittext tidak ada inputan ( kosong )
        if (name.isEmpty()) {
            //validasi
            if (validation()) {
                saveData(view = null)
            }
        }
    }

    private fun validation(): Boolean {
        if (binding.addUserName.text.toString().trim().isEmpty()) {
            binding.addUserNameTextInputLayout.error = "Username Tidak Boleh Kosong"
            binding.addUserName.requestFocus()
            return false
        } else {
            binding.addUserNameTextInputLayout.isErrorEnabled = false
        }
        return true
    }

    //TODO 30 Ketika Kita klik button Simpan Data maka data yang Kita inputkan akan tersimpan ke database
    fun saveData(view:View?) {
        //TODO 31 Membuat kondisi ketika inputan tidak kosong
        if (name.isNotEmpty()) {
            //TODO 32 mendeklarasikan variabel bioData
            val bioData = BioDataModel(name)
            //database tidak bisa diakses langsung di main thread utama
            //TODO 33 Kita perlu Executors agar dapat dieksekusi di tempat yang berbeda diluar AddDataActivity
            Executors.newSingleThreadExecutor().execute {
                //TODO 34 membuat proses menyimpan data bioData
                database.bioDataDao().insertData(bioData)
                //Ketika data berhasil tersimpan akan pindah ke halaman MainActivity
                startActivity<MainActivity>()
            }
            toast("data berhasil di simpan")
        }
    }
}






