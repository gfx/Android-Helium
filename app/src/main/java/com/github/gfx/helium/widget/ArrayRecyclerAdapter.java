package com.github.gfx.helium.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public abstract class ArrayRecyclerAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> implements Iterable<T> {

    final Context context;

    final ArrayList<T> list;

    OnItemClickListener<T> itemClickListener;

    OnItemLongClickListener<T> itemLongClickListener;

    public ArrayRecyclerAdapter(@NonNull Context context) {
        this.context = context;
        this.list = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @UiThread
    public void reset(Collection<T> items) {
        clear();
        addAll(items);
        notifyDataSetChanged();
    }

    public T getItem(int position) {
        return list.get(position);
    }

    public void addItem(T item) {
        list.add(item);
    }

    public void addAll(Collection<T> items) {
        list.addAll(items);
    }

    public void addAll(int position, Collection<T> items) {
        list.addAll(position, items);
    }

    @UiThread
    public void addAllWithNotification(Collection<T> items) {
        int position = getItemCount();
        addAll(items);
        notifyItemInserted(position);
    }

    public void clear() {
        list.clear();
    }

    public Context getContext() {
        return context;
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        itemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> listener) {
        itemLongClickListener = listener;
    }

    public void dispatchOnItemClick(View view, T item) {
        assert itemClickListener != null;
        itemClickListener.onItemClick(view, item);
    }

    public boolean dispatchOnItemLongClick(View view, T item) {
        assert itemLongClickListener != null;
        return itemLongClickListener.onItemLongClick(view, item);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
