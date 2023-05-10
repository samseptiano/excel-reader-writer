package com.example.assetmanagement.base.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<VB : ViewBinding, VH: RecyclerView.ViewHolder> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var viewBinding: VB? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        viewBinding = getBindingAdapter(parent)
        return getViewHolder(viewBinding!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bindVH(holder, position)
    }

    override fun getItemCount(): Int  = totalItem()

    internal abstract fun bindVH(holder: RecyclerView.ViewHolder, position: Int)
    internal abstract fun totalItem() : Int
    internal abstract fun getBindingAdapter(parent: ViewGroup): VB
    internal abstract fun getViewHolder(adapterBinding: VB): VH
}