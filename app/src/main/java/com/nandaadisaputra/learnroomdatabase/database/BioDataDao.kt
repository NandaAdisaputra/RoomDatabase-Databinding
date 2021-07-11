package com.nandaadisaputra.learnroomdatabase.database

import androidx.lifecycle.LiveData
import androidx.room.*

//TODO 16 Dao adalah kelas yang berisi fungsi untuk mengakses data pada database.
@Dao
interface BioDataDao {
    @Insert
    fun insertData(bioData: BioDataModel)

    @Query("SELECT * FROM BioDataModel")
    fun getAll(): LiveData<List<BioDataModel>>

    @Update
    fun update(bioData: BioDataModel)

    @Delete
    fun delete(bioData: BioDataModel)
}




