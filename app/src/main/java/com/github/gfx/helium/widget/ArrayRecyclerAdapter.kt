package com.github.gfx.helium.widget

import android.content.Context
import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.View

import java.util.ArrayList

abstract class ArrayRecyclerAdapter<T, VH : RecyclerView.ViewHolder>(val context: Context) : RecyclerView.Adapter<VH>(), Iterable<T> {

    internal val list: ArrayList<T>

    internal var itemClickListener: OnItemClickListener<T>? = null

    internal var itemLongClickListener: OnItemLongClickListener<T>? = null

    init {
        this.list = ArrayList<T>()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @UiThread
    fun reset(items: Collection<T>) {
        clear()
        addAll(items)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): T {
        return list[position]
    }

    fun addItem(item: T) {
        list.add(item)
    }

    fun addAll(items: Collection<T>) {
        list.addAll(items)
    }

    fun addAll(position: Int, items: Collection<T>) {
        list.addAll(position, items)
    }

    @UiThread
    fun addAllWithNotification(items: Collection<T>) {
        val position = itemCount
        addAll(items)
        notifyItemInserted(position)
    }

    fun clear() {
        list.clear()
    }

    fun setOnItemClickListener(listener: OnItemClickListener<T>) {
        itemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener<T>) {
        itemLongClickListener = listener
    }

    fun dispatchOnItemClick(view: View, item: T) {
        assert(itemClickListener != null)
        itemClickListener!!.onItemClick(view, item)
    }

    fun dispatchOnItemLongClick(view: View, item: T): Boolean {
        assert(itemLongClickListener != null)
        return itemLongClickListener!!.onItemLongClick(view, item)
    }

    override fun iterator(): Iterator<T> {
        return list.iterator()
    }
}
