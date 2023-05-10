package com.example.assetmanagement.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import java.util.*


fun String.convertStringToUpperCase(): String {
    /*
     * Program that first convert all uper case into lower case then
     * convert fist letter into uppercase
     */
    val calStr = this.split(" ").map { it.toLowerCase(Locale.ENGLISH).capitalize(Locale.ENGLISH) }
    return calStr.joinToString(separator = " ")
}

fun String.shortStringLength(): String {
    /*
     * Program that first convert all uper case into lower case then
     * convert fist letter into uppercase
     */
    var calStr = this
    if (this.length > 15)
        calStr = this.substring(0, 15).plus("...")
    return calStr
}

fun View.showSnackBar(message: String, action: String = "", actionListener: () -> Unit = {}): Snackbar {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
    if (action != "") {
        snackbar.duration = Snackbar.LENGTH_INDEFINITE
        snackbar.setAction(action) {
            actionListener()
            snackbar.dismiss()
        }
    }
    snackbar.show()
    return snackbar
}

fun EditText.hideKeyboard() {
    val keyboard: InputMethodManager? = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    keyboard?.hideSoftInputFromWindow(windowToken, 0)
}

fun EditText.typingListener(delay: Long = 1000L, onTypingRun: (String) -> Unit) {
    var timer = Timer()

    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if(!this@typingListener.hasFocus()) return
            timer.cancel()
            timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    onTypingRun(s.toString())
                }
            }, delay)
        }
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int){}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}