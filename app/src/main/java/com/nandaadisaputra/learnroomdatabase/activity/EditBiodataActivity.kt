package com.nandaadisaputra.learnroomdatabase.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.nandaadisaputra.learnroomdatabase.R
import com.nandaadisaputra.learnroomdatabase.database.BioDataModel
import com.nandaadisaputra.learnroomdatabase.database.MyDatabase
import com.nandaadisaputra.learnroomdatabase.databinding.ActivityEditBiodataBinding
import com.nandaadisaputra.learnroomdatabase.utils.BitmapOperator
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.io.File
import java.util.concurrent.Executors

//TODO 35 Membuat class EditBiodataActivity.kt
class EditBiodataActivity : AppCompatActivity() {
    //TODO 36 Lateinit memilki arti bahwa sebuah variabel akan di inisialisasi nanti di dalam onCreate
    // Kita deklarasikan variabel binding dan variabel database, private artinya variabel tersebut
    // hanya akan di akses di class EditBiodataActivity saja.
    private lateinit var binding: ActivityEditBiodataBinding
    private lateinit var database: MyDatabase
    private lateinit var photoFile: File
    private val photoName = "photo.jpg"
    private val TAG = "bioDataApp"
    //TODO 37 Kita deklarasikan variabel idBioData dan name
    var idBioData = 0
    var name = ""
    var photo = ""

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
        photoFile = getPhotoFileUri(photoName)
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

    //Returns the file for a photo stored on disk given the fileName
    private fun getPhotoFileUri(fileName: String): File {
        //Get safe storage directory for photos
        //Use 'getExternalFileDir' on Context to access package - specific directories
        //This way , we don't need to request external read/write runtime permissions
        val mediaStorageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)
        //create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdir()) {
            Log.d(TAG, "Failed to create directory")
        }
        //Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }
    //TODO 65 Kita perintah cek permission kamera
    private fun checkPermissionCamera(): Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }
    //TODO 66 Kita perintah cek permission gallery
    private fun checkPermissionGallery(): Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
    //TODO 67 Kita buat request permission kamera
    private fun requestPermissionCamera() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 100)
    }
    //TODO 68 Kita buat request permission gallery
    private fun requestPermissionGallery() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 110)
    }
    //TODO 69 Kita buat hasil request permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                openCamera()
            } else {
                toast("User tidak memberikan izin Camera")
            }
        } else if (requestCode == 110) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                openGallery()
            } else {
                toast("User tidak memberikan izin Gallery")
            }
        }
    }
    //TODO 70 Kita buat activityLauncher Cameranya
    private var activityLauncherCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
        binding.ivEditProfile.setImageBitmap(takenImage)
    }

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
    //TODO 62 Kita beri aksi klik ketika foto ingin diedit
   fun editPhotoProfile (view: View?){
            selectImage()
   }
    //TODO 64 Kita buat seleksi image nya menggunakan alert dialog
    private fun selectImage() {
        binding.ivEditProfile.setImageResource(0)
        val items = arrayOf<CharSequence>("Take Photo with Camera", "Choose from Gallery",
                "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@EditBiodataActivity)
        builder.setTitle("Add Photo Profile !")
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setItems(items) { dialog, item ->
            if (items[item] == "Take Photo with Camera") {
                if (checkPermissionCamera()) {
                    openCamera()
                } else {
                    requestPermissionCamera()
                }
            } else if (items[item] == "Choose from Gallery") {
                if (checkPermissionGallery()) {
                    openGallery()
                } else {
                    requestPermissionGallery()
                }
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }
    //TODO 71 Kita buat aksu di fungsi openCamera nya
    private fun openCamera() {
        val fileProvider = FileProvider.getUriForFile(this, "com.nandaadisaputra.fileprovider", photoFile)
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        }
        activityLauncherCamera.launch(takePictureIntent)
    }

    //TODO  72 Kita aksi di fungsi openGallery nya
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityLauncherGallery.launch(galleryIntent)
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