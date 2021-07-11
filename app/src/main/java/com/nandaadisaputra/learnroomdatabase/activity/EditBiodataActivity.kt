package com.nandaadisaputra.learnroomdatabase.activity

import android.content.Intent
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.nandaadisaputra.learnroomdatabase.R
import com.nandaadisaputra.learnroomdatabase.database.BioDataModel
import com.nandaadisaputra.learnroomdatabase.database.MyDatabase
import com.nandaadisaputra.learnroomdatabase.databinding.ActivityEditBiodataBinding
import com.nandaadisaputra.learnroomdatabase.utils.BitmapOperator
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
    var photo = ""

    //TODO 63  Kita buat activityLauncherGallery nya
    private var activityLauncherGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result.data?.data?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(this.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(this.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }
                binding.ivEditProfile.setImageBitmap(bitmap)
                photo = BitmapOperator().bitmapToString(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO 38 Kita inisialisasi binding nya
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_biodata)
        //TODO 40 menerima data yang Kita kirim dari MainActivity menggunakan Intent
        idBioData = intent.getIntExtra("id", 0)
        name = intent.getStringExtra("name") ?: ""
        //TODO 60 menerima data foto yang Kita kirim dari MainActivity menggunakan Intent
        photo = intent.getStringExtra("photo") ?:""
        //activity dari binding.activity = this diambil dari name variabel di activity_edit_biodata nya
        binding.activity = this
        //TODO 61 Kita tampilkan foto nya
        val bitmap = BitmapOperator().stringToBitmap(this, photo)
        binding.ivEditProfile.setImageBitmap(bitmap)
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
    //TODO 62 Kita beri aksi klik ketika foto ingin diedit
   fun editPhotoProfile (view: View?){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        //TODO 64 Kita aktifkan intentnya biar bisa jalan
        activityLauncherGallery.launch(galleryIntent)
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
        if (name.isNotEmpty() && photo.isNotEmpty() ) {
            //TODO 45 mendeklarasikan variabel bioData
            val bioData = BioDataModel(name,photo).apply {
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
        val bioData = BioDataModel(name,"").apply {
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