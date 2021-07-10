package com.jlp.unforgotchi.locations

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jlp.unforgotchi.R
import com.jlp.unforgotchi.db.Location
import java.io.File

class LocationsAdapter(
    var mList: List<Location>,
    val itemClickListener: OnItemClickListener
    ) : RecyclerView.Adapter<LocationsAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class
        if(itemsViewModel.image != null) {
            Glide.with(holder.itemView.context)
                .load(Uri.parse(itemsViewModel.image!!))
                .into(holder.imageView)
        } else {
            holder.imageView.setImageResource(R.drawable.ic_baseline_location_city_24)
        }
        // sets the text to the textview from our itemHolder class
        holder.textView.text = itemsViewModel.text
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) ,
        View.OnClickListener {
            val imageView: ImageView = itemView.findViewById(R.id.location_image)
            val textView: TextView = itemView.findViewById(R.id.location_name)
            init {
                itemView.setOnClickListener(this)
        }
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
    fun setData(locationsList: List<Location>){
        mList = locationsList
        notifyDataSetChanged()
    }
}