package com.example.assetmanagement.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import com.example.assetmanagement.R

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