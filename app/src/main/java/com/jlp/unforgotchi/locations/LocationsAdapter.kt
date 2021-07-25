package com.jlp.unforgotchi.locations

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jlp.unforgotchi.R
import com.jlp.unforgotchi.db.Location

//This is the Adaper for the Recycler View in the Locations activity
class LocationsAdapter(
    var listOfLocations: List<Location>,
    val itemClickListener: OnItemClickListener
    ) : RecyclerView.Adapter<LocationsAdapter.ViewHolder>() {

    //For creating new Views for the Locations:
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Inflates the location_item view, where Locations are being displayed
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_item, parent, false)
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = listOfLocations[position]

        //Set the image of the ImageView of the ViewHolder class at the bottom:
        if(itemsViewModel.image != null) {
            Glide.with(holder.itemView.context)
                .load(Uri.parse(itemsViewModel.image!!))
                .into(holder.imageView)
        } else {
            holder.imageView.setImageResource(R.drawable.ic_baseline_location_city_24)
        }
        //Set the text of the TextView of the Viewholder class at the bottom:
        holder.textView.text = itemsViewModel.text
    }

    //The number of Locations:
    override fun getItemCount(): Int {
        return listOfLocations.size
    }

    //Holds the Views for the image and text of the Locations:
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) ,
        View.OnClickListener {
            val imageView: ImageView = itemView.findViewById(R.id.location_image)
            val textView: TextView = itemView.findViewById(R.id.location_name)
            init {
                itemView.setOnClickListener(this)
        }
        //Makes clicking items possible:
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                itemClickListener.onItemClick(position)
            }
        }
    }
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    //Used to fill the Adapter (and thus the RecyclerView) with all the Locations from the database:
    fun setData(locationsList: List<Location>){
        listOfLocations = locationsList
        notifyDataSetChanged()
    }
}