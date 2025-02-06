package com.hontail.ui.picture

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemCocktailPictureResultFlexBinding

class PictureTextAdapter(val context: Context, var textList: List<String>) : RecyclerView.Adapter<PictureTextAdapter.PictureTextHolder>() {

    inner class PictureTextHolder(private val binding: ListItemCocktailPictureResultFlexBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(name: String){
            binding.textViewCocktailPictureResultFlexName.text = name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureTextHolder {
        val binding = ListItemCocktailPictureResultFlexBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PictureTextHolder(binding)
    }

    override fun getItemCount(): Int {
        return textList.size
    }

    override fun onBindViewHolder(holder: PictureTextHolder, position: Int) {
        holder.bind(textList[position])
    }


}