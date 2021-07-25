package com.jlp.unforgotchi.locations

import com.jlp.unforgotchi.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView

//This custom dropdown adapter is heavily inspired by Tyler V's answer at
//https://stackoverflow.com/questions/38417984/android-spinner-dropdown-checkbox (last access Jul 9th 2021).
//Currently, this adaper is not in use. However, it is planned to be integrated into the app in a
//later update.
//This dropdown adapter enables selecting multiple lists per location.
class DropDownAdapter<T> internal constructor(
    private val context: Context,
    private val headerText: String,
    all_items: List<DropDownItem<T>>,
    selected_items: MutableSet<T>
) : BaseAdapter() {
    private val selectedItems: MutableSet<T> = selected_items
    private val allItems: List<DropDownItem<T>> = all_items

    override fun getView(
        position: Int,
        dropDownView: View?,
        parent: ViewGroup
    ): View? {
        var dropDownView: View? = dropDownView
        val holder: ViewHolder
        if (dropDownView == null) {
            dropDownView = LayoutInflater
                            .from(context)
                            .inflate(R.layout.select_lists_for_location, parent, false)
            holder = ViewHolder()
            holder.textView = dropDownView.findViewById(R.id.select_list_for_location_text)
            holder.checkBox = dropDownView.findViewById(R.id.select_list_for_location_checkbox)
            dropDownView.tag = holder
        } else {
            holder = dropDownView.tag as DropDownAdapter<T>.ViewHolder
        }
        if (position < 1) {
            holder.checkBox?.visibility = View.GONE
            holder.textView?.text = headerText
        } else {
            val listPos = position - 1
            holder.checkBox?.visibility = View.VISIBLE
            holder.textView?.text = allItems[listPos].txt
            val item = allItems[listPos].item
            val isSelected = selectedItems.contains(item)
            holder.checkBox?.setOnCheckedChangeListener(null)
            holder.checkBox?.isChecked = isSelected
            holder.checkBox?.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedItems.add(item)
                } else {
                    selectedItems.remove(item)
                }
            }
            holder.textView?.setOnClickListener {
                holder.checkBox?.toggle()
            }
        }
        return dropDownView
    }

    override fun getCount(): Int {
        return allItems.size + 1
    }

    override fun getItem(position: Int): Any? {
        return if (position < 1) {
            null
        } else {
            allItems[position - 1]
        }
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    internal class DropDownItem<T>(val item: T, val txt: String)

    private inner class ViewHolder {
        var textView: TextView? = null
        var checkBox: CheckBox? = null
    }
}