package com.techteam.aqproad.Item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.bumptech.glide.Glide
import com.techteam.aqproad.R
import com.techteam.aqproad.databinding.ElementoCarouselBinding

class CarouselAdapter (private val images: List<String>) :
    RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    inner class CarouselViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        private val carouselImageView: AppCompatImageView = view.findViewById(R.id.carouselImageView)

        fun bind(imageURL : String) {
            carouselImageView.load(imageURL) {
                transformations(RoundedCornersTransformation(8f))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        return CarouselViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.elemento_carousel, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.bind(images[position])
    }

}