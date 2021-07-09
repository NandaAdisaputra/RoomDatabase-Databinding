package com.nandaadisaputra.learnroomdatabase.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.nandaadisaputra.learnroomdatabase.R
import com.nandaadisaputra.learnroomdatabase.database.BioDataModel
import com.nandaadisaputra.learnroomdatabase.databinding.ItemLayoutBinding

//TODO 18 Membuat Class BioDataAdapter.kt
class BioDataAdapter(private val items: List<BioDataModel>, private  val onItemClick : (bioData : BioDataModel) -> Unit) : RecyclerView.Adapter<BioDataAdapter.BioDataViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BioDataViewHolder {
        return BioDataViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_layout, parent, false))
    }
    override fun onBindViewHolder(holder: BioDataViewHolder, position: Int) {
        holder.bind(items[position])
        //Kita buat fungsi onClick
        holder.itemView.setOnClickListener {
            onItemClick(items[position])
        }
    }
    //Untuk menentukan berapa data list yang akan di tampilkan
    //items.size artinya data akan ditampilkan semuanya
    override fun getItemCount(): Int {
        return items.size
    }
    class BioDataViewHolder(var itemLayoutBinding: ItemLayoutBinding) : RecyclerView.ViewHolder(itemLayoutBinding.root) {
        fun bind(bioData: BioDataModel?) {
            itemLayoutBinding.bioData = bioData
            itemLayoutBinding.executePendingBindings()
        }
    }
}




