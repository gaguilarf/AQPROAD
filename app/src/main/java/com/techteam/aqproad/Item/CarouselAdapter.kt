package com.techteam.aqproad.Item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.techteam.aqproad.databinding.ElementoCarouselBinding

class CarouselAdapter(private val imageList: MutableList<String>) :
    RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    inner class CarouselViewHolder (private val binding: ElementoCarouselBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image : String) {
            Glide.with(binding.imagenCarousel.context)
                .load(image)
                .into(binding.imagenCarousel)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        return CarouselViewHolder(
            ElementoCarouselBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false))
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.bind(imageList[position])
    }
}