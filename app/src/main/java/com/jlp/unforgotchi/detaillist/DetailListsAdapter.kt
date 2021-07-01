package com.jlp.unforgotchi.detaillist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jlp.unforgotchi.R
import com.jlp.unforgotchi.db.ReminderList
import com.jlp.unforgotchi.db.ReminderListElement

class DetailListsAdapter(
    //private val thisList: ArrayList<DetailListsItemsVM>,
    private val listener: OnItemClickListener
    )
    : RecyclerView.Adapter<DetailListsAdapter.ViewHolder>() {

        private var reminderElementsList: List<ReminderListElement> = emptyList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            // inflates the card_view_design view
            // that is used to hold list item
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.detail_lists_item, parent, false)

            return ViewHolder(view)
        }

        // binds the list items to a view
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val currentItem = reminderElementsList[position]

            // sets the text to the textView from our itemHolder class
            holder.textView.text = currentItem.listElementName
            holder.index.text = currentItem.id.toString()

        }

        // return the number of the items in the list
        override fun getItemCount(): Int {
            return reminderElementsList.size
        }

        // Holds the views for adding it to image and text
        inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView),
        View.OnClickListener {
            val textView: TextView = itemView.findViewById(R.id.item_name)
            val index: TextView = itemView.findViewById(R.id.item_index)

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

    fun setData(reminderListElements: List<ReminderListElement>){
        reminderElementsList = reminderListElements
        notifyDataSetChanged()
    }
}
