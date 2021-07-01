package com.jlp.unforgotchi.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jlp.unforgotchi.R
import com.jlp.unforgotchi.db.ReminderList

class ListsAdapter (
    /*private var reminderListsList: List<ReminderList>,*/
    private val listener: OnItemClickListener
)
    : RecyclerView.Adapter<ListsAdapter.ViewHolder>() {

    private var reminderListsList: List<ReminderList> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lists_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem = reminderListsList[position]

        // sets the Id to the textView from our itemHolder class
        holder.indexView.text = currentItem.id.toString()

        // sets the image to the imageView from our itemHolder class
        holder.imageView.setImageResource(currentItem.image)

        // sets the text to the textView from our itemHolder class
        holder.textView.text = currentItem.listName

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return reminderListsList.size
    }

    // Holds the views for adding it to image and text
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView),
        View.OnClickListener {
        val indexView: TextView = itemView.findViewById(R.id.lists_number)
        val imageView: ImageView = itemView.findViewById(R.id.lists_image)
        val textView: TextView = itemView.findViewById(R.id.lists_name)

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setData(reminderList: List<ReminderList>){
        reminderListsList = reminderList
        notifyDataSetChanged()
    }
}