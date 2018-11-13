package com.example.ann.photos

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item.view.*

class ListAdapter(val onClick: (Int) -> Any, val items: Array<String?>): RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private val LOG_TAG = Loader::class.java.simpleName

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val inflater = LayoutInflater.from(p0.context)
        val view = inflater.inflate(R.layout.list_item, p0, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setElement(p0: Int, p1: String) {
        items[p0] = p1
        notifyItemChanged(p0)
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        if (items[p1] == null) return
        p0.textView.text = items[p1]
        p0.textView.setOnClickListener {
            onClick(p1)
        }
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textView = view.textView
    }
}