package com.example.assetmanagement.utils

import android.app.DatePickerDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    fun getDatePicker(context: Context, callBack:(String)-> Unit){
        val picker:DatePickerDialog
        val cldr: Calendar = Calendar.getInstance()
        val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
        val month: Int = cldr.get(Calendar.MONTH)
        val year: Int = cldr.get(Calendar.YEAR)
        picker = DatePickerDialog(context,
            { _, year, month, dayOfMonth ->

                val currMonth = month + 1
                val formattedCurMonth = if(currMonth < 10) "0$currMonth" else currMonth
                val formattedCurDay = if(dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth

                val stringDate = "$year-${formattedCurMonth}-$formattedCurDay"
                callBack(stringDate)
            }, year, month, day)
        picker.show()

    }

    fun compareTwoDate(startDate:String, endDate:String):Boolean{
        val start: Date = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            .parse(startDate) as Date
        val end: Date = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            .parse(endDate) as Date
        return start <= end
    }
}