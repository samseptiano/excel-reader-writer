package com.example.assetmanagement.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.example.assetmanagement.data.model.ListItems
import com.example.assetmanagement.data.model.SingleRow
import com.example.assetmanagement.databinding.CellListitemBinding
import com.example.assetmanagement.ui.ExcelCellEditListener
import com.example.assetmanagement.ui.ExcelRowClickListener
import com.example.assetmanagement.ui.ExcelRowEditListener


class ListItemAdapter(
    private val exampleList: ArrayList<ListItems>,
    private val listener: ExcelRowClickListener,
    private val editListener: ExcelRowEditListener

) :
    RecyclerView.Adapter<ListItemAdapter.ExampleViewHolder>() {
    var exampleListFull = ArrayList<ListItems>()

    companion object {
        var searchString: String? = null
    }

    init {
        exampleListFull = exampleList
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
                searchString = constraint.toString()
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    exampleListFull = exampleList
                } else {
                    val resultList = ArrayList<ListItems>()
                    resultList.addAll(exampleList.filter {
                        it.singleRowList.filter {
                            it.value?.contains(charSearch, true) == true
                        }.isNotEmpty()
                    })

                    exampleListFull = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = exampleListFull
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                exampleListFull = results?.values as ArrayList<ListItems>
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        return ExampleViewHolder(
            CellListitemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = exampleListFull.size

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val currentItem = exampleListFull[position]
        val adapter = Adapter(
            currentItem.singleRowList.orEmpty() as ArrayList<SingleRow>,
            object : ExcelRowClickListener {
                override fun onRowClick(position1: Int) {
                    //list change status
//                    listener.onRowClick(position)
//                    val status = currentItem.singleRowList.last().value
//                    if (status?.contentEquals(Constant.FLAG_COMPLETE) == true) {
//                        currentItem.singleRowList.last().value = Constant.FLAG_PENDING
//                    } else {
//                        currentItem.singleRowList.last().value = Constant.FLAG_COMPLETE
//                    }
//                    notifyItemChanged(position)
                }
            },
            object : ExcelCellEditListener {
                override fun onCellEdit(cell: Int, singleRow: SingleRow) {
                    currentItem.singleRowList[cell] = singleRow
                    editListener.onRowEdit(
                        cell, position,
                        singleRow
                    )
                }
            })
        holder.binding.recyclerView.adapter = adapter
//        if (currentItem.singleRowList.last().value.equals(Constant.FLAG_COMPLETE)) {
//            holder.binding.oneRowCard.setBackgroundColor(Color.YELLOW)
//        }
//        if (currentItem.singleRowList.last().value.equals(Constant.FLAG_PENDING)) {
//            holder.binding.oneRowCard.setBackgroundColor(Color.WHITE)
//        }

        holder.binding.btnDelete.setOnClickListener {
            exampleListFull.remove(currentItem)
            notifyItemRemoved(position)
        }
        holder.binding.btnUpdate.setOnClickListener {
            updateData(position, currentItem)
        }
    }

    private fun updateData(position: Int, currentItem: ListItems) {
        exampleListFull[position] = currentItem
        notifyItemChanged(position)
    }

    inner class ExampleViewHolder(val binding: CellListitemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    fun clear() {
        val oldSize = exampleListFull.size
        exampleListFull.clear()
        notifyItemRangeRemoved(0, oldSize)
    }

    fun setData(list: List<ListItems>) {
        this.exampleListFull.addAll(list)
        notifyItemRangeInserted(0, exampleListFull.size)
    }
}