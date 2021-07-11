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
import com.nandaadisaputra.learnroomdatabase.databinding.ActivityAddDataBinding
import com.nandaadisaputra.learnroomdatabase.utils.BitmapOperator
import org.jetbrains.anko.toast
import java.io.File
import java.util.concurrent.Executors


//TODO 23 Membuat class AddDataActivity.kt
class AddBioDataActivity : AppCompatActivity() {
    //TODO 25 Lateinit memilki arti bahwa sebuah variabel akan di inisialisasi nanti di dalam onCreate
    // Kita deklarasikan variabel binding dan variabel database, private artinya variabel tersebut
    // hanya akan di akses di class AddDataActivity saja.
    private lateinit var binding: ActivityAddDataBinding
    private lateinit var database: MyDatabase
    private lateinit var photoFile: File
    private val photoName = "photo.jpg"
    private val TAG = "bioDataApp"

    //TODO 26 Kita deklarasikan variabel name
    var name = ""

    //TODO 55  Kita deklarasikan variabel photo
    var photo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO 27 Kita inisialisasi binding nya
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_data)
        //activity dari binding.activity = this diambil dari name variabel di activity_add_data nya
        binding.activity = this
        photoFile = getPhotoFileUri(photoName)
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
    //TODO 74 Kita buat cek permission kamera nya
    private fun checkPermissionCamera(): Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }
    //TODO 75 Kita buat cek permission galeri nya
    private fun checkPermissionGallery(): Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
    //TODO 76 Kita buat request permission kamera nya
    private fun requestPermissionCamera() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 100)
    }
    //TODO 77 Kita buat request permission galeri nya
    private fun requestPermissionGallery() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 110)
    }
    //TODO 78 Kita buat hasil request permissionnya
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
    //TODO 79 Kita buat activityLauncherCamera nya
    private var activityLauncherCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
        binding.ivAddProfile.setImageBitmap(takenImage)
    }

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
    //TODO 80 Kita buat aksi ketika gambar foto di klik
    fun addPhotoProfile(view: View?) {
        selectImage()
    }
    //TODO 81 Kita buat seleksi image menggunakan alert dialog
    private fun selectImage() {
        binding.ivAddProfile.setImageResource(0)
        val items = arrayOf<CharSequence>("Take Photo with Camera", "Choose from Gallery",
                "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@AddBioDataActivity)
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
    //TODO 82 Kita aksi di fungsi openCamera
    private fun openCamera() {
        val fileProvider = FileProvider.getUriForFile(this, "com.nandaadisaputra.fileprovider", photoFile)
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        }
        activityLauncherCamera.launch(takePictureIntent)
    }

    //TODO 57  Kita aksi di fungsi openGallery
    private fun openGallery() {
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






