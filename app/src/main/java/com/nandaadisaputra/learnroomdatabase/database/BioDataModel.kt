package com.nandaadisaputra.learnroomdatabase.database

import androidx.room.Entity
import androidx.room.PrimaryKey

//TODO 15 Entity merupakan sebuah tabel dalam database. Room menciptakan sebuah tabel
// untuk setiap class yang memiliki anotasi @Entity , property dalam sebuah class
// mewakili kolom kolom pada tabel.
@Entity
//TODO 54 Tambahkan param photo dengan tipe String
data class BioDataModel(var name: String, var photo : String) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}



