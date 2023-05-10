package com.example.assetmanagement.utils

import android.view.View
import android.view.WindowManager
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


fun View.setBottomsheetWrapContent(): View {
    val layoutParams = this.layoutParams
    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
    this.layoutParams = layoutParams
    return this
}

fun Int.formatRupiah() : String {
    val localeID = Locale("in", "ID")
    val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
    return formatRupiah.format(this)
}

fun getCurrentDate() : String {
    val c = Calendar.getInstance().time
    val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return df.format(c)
}