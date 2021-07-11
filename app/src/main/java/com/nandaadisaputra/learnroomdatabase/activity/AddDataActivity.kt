package com.nandaadisaputra.learnroomdatabase.activity

import android.content.Intent
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.nandaadisaputra.learnroomdatabase.R
import com.nandaadisaputra.learnroomdatabase.database.BioDataModel
import com.nandaadisaputra.learnroomdatabase.database.MyDatabase
import com.nandaadisaputra.learnroomdatabase.databinding.ActivityAddDataBinding
import com.nandaadisaputra.learnroomdatabase.utils.BitmapOperator
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
    //TODO 55  Kita deklarasikan variabel photo
    var photo = ""

//TODO 56  Kita buat activityLauncherGallery nya
    private var activityLauncherGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result.data?.data?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(this.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(this.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }
                binding.ivAddProfile.setImageBitmap(bitmap)
                photo = BitmapOperator().bitmapToString(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
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
    //TODO 57  Kita aksi ketika image foto profile di klik
    fun openGallery(view: View?) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        //TODO 58 Kita aktifkan intentnya biar bisa jalan
        activityLauncherGallery.launch(galleryIntent)
    }


    //TODO 30 Ketika Kita klik button Simpan Data maka data yang Kita inputkan akan tersimpan ke database
    fun saveData(view: View?) {
        Log.d("tes data", "$name $photo")
        //TODO 31 Membuat kondisi ketika inputan tidak kosong
        if (name.isNotEmpty() && photo.isNotEmpty()) {
            //TODO 32 mendeklarasikan variabel bioData
            val bioData = BioDataModel(name, photo)
            //database tidak bisa diakses langsung di main thread utama
            //TODO 33 Kita perlu Executors agar dapat dieksekusi di tempat yang berbeda diluar AddDataActivity
            Executors.newSingleThreadExecutor().execute {
                //TODO 34 membuat proses menyimpan data bioData
                database.bioDataDao().insertData(bioData)
            }
            toast("data berhasil di simpan")
            finish()
        }
    }
}






