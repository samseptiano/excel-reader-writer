package com.example.assetmanagement.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import androidx.core.content.FileProvider
import com.example.assetmanagement.BuildConfig
import com.example.assetmanagement.R
import java.io.File


/**
 * Created by samuel.septiano on 05/05/2023.
 */
 fun Context.startDownloadFile(url: String?) {
    val fileName = URLUtil.guessFileName(url, null, MimeTypeMap.getFileExtensionFromUrl(url))
    val uri = Uri.parse(url)
    val request = DownloadManager.Request(uri)
    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
    request.setTitle(fileName)
    request.setDescription(getString(R.string.file_is_downloading))
    request.allowScanningByMediaScanner()
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    request.setDestinationInExternalPublicDir(
        Environment.DIRECTORY_DOWNLOADS,
        uri.lastPathSegment
    )
    val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    manager.enqueue(request)
}

fun enableStrictMode(){
    val builder = VmPolicy.Builder()
    StrictMode.setVmPolicy(builder.build())
}

fun Context.getTmpFileUri(): Uri {
    val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }
    return FileProvider.getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
}
