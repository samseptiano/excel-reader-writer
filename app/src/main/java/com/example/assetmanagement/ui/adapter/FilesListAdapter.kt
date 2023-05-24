package com.example.assetmanagement.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assetmanagement.data.model.ExcellFile
import com.example.assetmanagement.databinding.ListViewBinding
import com.example.assetmanagement.ui.FilesListActivity
import com.example.assetmanagement.ui.MainActivity
import com.example.assetmanagement.utils.Constant.Companion.PATH_IMPORT_EXCEL
import java.io.File

class FilesListAdapter(private val context: Context) :
    RecyclerView.Adapter<FilesListAdapter.MyViewHolder>() {
    private val files: MutableList<ExcellFile> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ListViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return files.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem: ExcellFile = files[position]
        holder.binding.tvTitle.text = currentItem.name

        holder.binding.root.setOnClickListener {
            openFile(currentItem.path)

        }
        holder.binding.ivDelete.setOnClickListener {
            deleteSelectedFile(currentItem.path)
        }
    }

    private fun deleteSelectedFile(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
        if (context is FilesListActivity) {
            context.getFiles()
        }
    }

    private fun openFile(path: String) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(PATH_IMPORT_EXCEL, path)
        context.startActivity(intent)

    }

    class MyViewHolder(val binding: ListViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val textView1: TextView = binding.tvTitle
    }

    fun View.gone() {
        this.visibility = View.GONE
    }

    fun View.visible() {
        this.visibility = View.VISIBLE
    }

    fun View.invisible() {
        this.visibility = View.INVISIBLE
    }

    fun clear() {
        val oldSize = files.size
        files.clear()
        notifyItemRangeRemoved(0, oldSize)
    }

    fun setData(list: MutableList<ExcellFile>) {
        this.files.addAll(list)
        notifyItemRangeInserted(0, files.size)
    }

}
