package com.nandaadisaputra.learnroomdatabase.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.nandaadisaputra.learnroomdatabase.R
import com.nandaadisaputra.learnroomdatabase.adapter.BioDataAdapter
import com.nandaadisaputra.learnroomdatabase.database.BioDataModel
import com.nandaadisaputra.learnroomdatabase.database.MyDatabase
import com.nandaadisaputra.learnroomdatabase.databinding.ActivityMainBinding
import org.jetbrains.anko.longToast
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {
    //  TODO 19 Lateinit memilki arti bahwa sebuah variabel akan di inisialisasi nanti di dalam onCreate
    //   Kita deklarasikan variabel binding dan variabel database
    //   private artinya variabel tersebut hanya akan di akses di class MainActivity saja.
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: MyDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO 20 Kita inisialisasi binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this
        //TODO 21 Kita inisialisasi database
        database = MyDatabase.getDatabase(this)
        //TODO 22 Kita akan menampilkan data yang disimpan ke dalam adapter berupa list data
        database.bioDataDao().getAll().observe(this, {
            //adapter dari  binding.adapter = BioDataAdapter(it) diambil dari name variabel di activity_main nya
            binding.adapter = BioDataAdapter(it) {
                //TODO 39 Kita kirimkan data (parsing data ) yang akan di edit ke  EditBiodataActivity menggunakan intent
                val intent = Intent(this, EditBiodataActivity::class.java).apply {
                    putExtra("id", it.id)
                    putExtra("name", it.name)
                    //TODO 59  Kita tambahkan intent dengan data dengan membawa data photo
                    putExtra("photo", it.photo)
                }
                startActivity(intent)
            }
        })
    }
    //TODO 51 Buatlah logic switch ketika switch ada aksi klik
    fun switchDarkMode(isChecked: Boolean) {
            if (binding.switchDarkMode.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    //TODO 24 Ketika Kita klik button tambah Data maka akan melakukan intent ( perpindahan ) dari class MainActivity
    // ke class AddDataActivity
    fun addData(view: View) {
        // Kita intent kan menggunakan library anko common yang telah Kita tambahkan di build gradle
        // agar lebih ringkas
        startActivity<AddDataActivity>()
    }
}



