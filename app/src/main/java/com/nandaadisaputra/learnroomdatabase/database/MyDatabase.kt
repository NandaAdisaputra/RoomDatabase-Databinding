package com.nandaadisaputra.learnroomdatabase.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//TODO 17 Database merupakan kelas yang menyediakan
// akses / koneksi kepada database yang Kita buat
//Untuk membuat sebuah database, Kita perlu mendefinisikan sebuah abstract class yang mengextend
//RoomDatabase dengan memberikan anotasi @Database, daftar dari entities yang akan dimasukkan
// ke dalam database, dan masing - masing DAO
@Database(
        entities = [BioDataModel::class],
        //versi database
        version = 3,
        // exportSchema untuk export jadi file, misal file JSON
        exportSchema = false
)
abstract class MyDatabase : RoomDatabase() {
    //Kita daftarkan interface BioDataDao
    abstract fun bioDataDao(): BioDataDao

    //companion object untuk mengembalikan database itu sendiri ya teman"
    companion object {
        //INSTANCE Singleton, agar ketika buka aplikasi cukup mengakses cukup 1 database saja bukan akses databasenya
        // lebih dari satu.
        @Volatile
        //Inisialisasi Database nya
        private var INSTANCE: MyDatabase? = null
        //getDatabase merupakan fungsi yang akan Kita panggil dari aplikasi,
        // mengembalikan class MyDatabase
        fun getDatabase(context: Context): MyDatabase {
            val tempInstance = INSTANCE
            //Jika INSTANCE tidak null maka akan langsung di kembalikan INSTANCE nya sendiri
            if (tempInstance != null) {
                //kembalikan INSTANCE nya sendiri
                return tempInstance
                //ketika sudah di kembalikan INSTANCE nya sendiri, maka fungsi synchronized(this) dibawahnya tidak
                // akan dieksekusi karena sudah ada database nya.
            }
            //Jika eh ternyata instance nya masih null maka Kita buat databasenya dengan mengeksekusi perintah di bawah ini.
            synchronized(this) {
                //databaseBuilder merupakan proses membuat bioData_database
                val instance = Room.databaseBuilder(context.applicationContext, MyDatabase::class.java, "bioData_database")
                        //dengan .fallbackToDestructiveMigration() Jika versi nya naik perlu migrasi maka database akan di clear / hapus
                        //Hal ini terjadi ketika terjadi penambahan properti / parameter data class Model. maupun perubahan versi database
                        .fallbackToDestructiveMigration()
                        //Kemudian Kita buat databasenya
                        .build()
                INSTANCE = instance
                //Kita mengembalikan instance
                return instance
            }
        }
    }
}








