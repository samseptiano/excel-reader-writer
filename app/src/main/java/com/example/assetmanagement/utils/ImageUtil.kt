package com.example.assetmanagement.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import com.yalantis.ucrop.UCrop
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by samuel.septiano on 23/05/2023.
 */
fun Context.getTempImageUri():Uri{
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val tempCropped = File(cacheDir, "tempImgCropped-${timeStamp}.jpg")
    return Uri.fromFile(tempCropped)
}

fun Activity.callUCropSquare(sourceUri:Uri, destinationUri:Uri){
    UCrop.of(sourceUri, destinationUri)
        .withAspectRatio(1f, 1f)
        .withMaxResultSize(1000, 1000)
        .start(this);
}