package com.example.assetmanagement.ui.adapter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.assetmanagement.R
import com.example.assetmanagement.data.model.SingleRow
import com.example.assetmanagement.databinding.CustomLayoutBinding
import com.example.assetmanagement.ui.ExcelCellEditListener
import com.example.assetmanagement.ui.ExcelCellImageListener
import com.example.assetmanagement.ui.ExcelRowClickListener
import com.example.assetmanagement.utils.isNumeric
import java.math.BigDecimal
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class Adapter(
    private val exampleList: ArrayList<SingleRow>,
    private val listener: ExcelRowClickListener,
    private val editListener: ExcelCellEditListener,
    private val imageListener: ExcelCellImageListener

) : RecyclerView.Adapter<Adapter.ExampleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        return ExampleViewHolder(
            CustomLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = exampleList.size

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val currentItem = exampleList[position]
        holder.binding.apply {
            textView1.text = currentItem.name
            if (currentItem.value?.contains("http") == true) {
                textView2.visibility = View.GONE
                ivImageLink.visibility = View.VISIBLE
                val options: RequestOptions = RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.bg_black_stroke)
                    .error(R.drawable.bg_rounded_gray)
                Glide.with(ivImageLink.context).load(currentItem.value).apply(options)
                    .into(ivImageLink);
                ivImageLink.setOnClickListener {
                    imageListener.onCellImageEdit(0,position, currentItem)

                }
            } else {
                textView2.visibility = View.VISIBLE
                ivImageLink.visibility = View.GONE
                if (currentItem.value?.isNumeric() == true) {
                    textView2.setText(BigDecimal(currentItem.value!!.toDouble()).toPlainString())
                } else {
                    textView2.setText(currentItem.value)

                }
            }
            textView2.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    currentItem.value = textView2.text.toString()
                    editListener.onCellEdit(position, currentItem)
                }
            }

            if (ListItemAdapter.searchString != null && !ListItemAdapter.searchString!!.isEmpty()) {
                val sb = SpannableStringBuilder(currentItem.value)
                val word: Pattern =
                    Pattern.compile(ListItemAdapter.searchString!!.lowercase(Locale.ROOT))
                val match: Matcher = word.matcher(currentItem.value?.lowercase(Locale.ROOT))
                while (match.find()) {
                    val fcs = ForegroundColorSpan(Color.WHITE)
                    sb.setSpan(fcs, match.start(), match.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    val bcs = BackgroundColorSpan(Color.DKGRAY)
                    sb.setSpan(bcs, match.start(), match.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }
                holder.binding.textView2.setText(sb.toString())
            } else {
                holder.binding.textView2.setText(currentItem.value)
            }
        }
    }

    inner class ExampleViewHolder(val binding: CustomLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    listener.onRowClick(adapterPosition)
                }
            }
        }
    }

}