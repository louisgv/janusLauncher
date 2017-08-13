package com.blueberry.lab.januslauncher

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by jojo on 8/4/17.
 */

class AppListAdapter (context : Context)
    : ArrayAdapter<AppModel> (context, R.layout.list_item) {

    val inflater = LayoutInflater.from(context)!!

    fun setData(data: Array<AppModel>?) {
        clear()
        if (data != null) {
            addAll(data.toMutableList())
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View = convertView ?:
                inflater.inflate(R.layout.list_item, parent, false)

        val item = getItem(position)

        view.findViewById<ImageView>(R.id.item_app_icon).setImageDrawable(item.icon)
        view.findViewById<TextView>(R.id.item_app_label).text = item.appLabel

        return view
    }
}