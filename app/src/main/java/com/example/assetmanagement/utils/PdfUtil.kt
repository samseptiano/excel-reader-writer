package com.example.assetmanagement.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.assetmanagement.R
import com.example.assetmanagement.data.model.ListItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by samuel.septiano on 17/05/2023.
 */
suspend fun Context.generatePDF(titlePdf:String, listItem: ArrayList<ListItems>): File? {
    var pageNumber = 1
    val pageHeight = 1120
    val pageWidth = 792

    var createdFile:File? = null

    val pdfDocument = PdfDocument()
    val bmp: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.excel)
    val scaledbmp = Bitmap.createScaledBitmap(bmp, 60, 60, false)

    var paint = Paint()
    var title = Paint()

    var myPageInfo: PdfDocument.PageInfo? =
        PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()


    var myPage: PdfDocument.Page = pdfDocument.startPage(myPageInfo)

    var canvas: Canvas = myPage.canvas

    canvas.drawBitmap(scaledbmp, 56F, 40F, paint)

    title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

    title.textSize = 20F
    title.color = ContextCompat.getColor(this, R.color.black)
    canvas.drawText("Report Asset Pt. Rodamas Group", 165F, 60F, title)

    title.textSize = 16F
    title.color = ContextCompat.getColor(this, R.color.purple_200)
    canvas.drawText("Data Per 12/5/2023", 165F, 80F, title)
    title.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
    title.color = ContextCompat.getColor(this, R.color.purple_200)
    title.textSize = 14F


    title.textAlign = Paint.Align.LEFT

    val x = 30f
    var y = 130f

    for (i in listItem.indices) {
        y += 30F
        if (y >= 1000F) {
            pdfDocument.finishPage(myPage)

            myPageInfo =
                PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber++).create()
            myPage = pdfDocument.startPage(myPageInfo)
            canvas = myPage.canvas

            paint = Paint()
            title = Paint()

            title.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            title.color = ContextCompat.getColor(this, R.color.purple_200)
            title.textSize = 14F
            title.textAlign = Paint.Align.LEFT
            y = 80F
        }
        for (j in listItem[i].singleRowList.indices) {
            if(listItem[i].singleRowList[j].value?.contains("http") == true){
                canvas.drawText(
                    "${listItem[i].singleRowList[j].name}:\t",
                    x,
                    y,
                    title
                )
                y += 16F

                try {
                    withContext(Dispatchers.IO) {
                        paint = Paint()
                        val url = URL(listItem[i].singleRowList[j].value)
                        val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                        val scaledImage = Bitmap.createScaledBitmap(image, 250, 150, false)
                        canvas.drawBitmap(scaledImage, x, y, paint)
                        y += 150F
                    }
                } catch (e: IOException) {
                    println(e)
                }



            }else {
                canvas.drawText(
                    "${listItem[i].singleRowList[j].name}:\t ${listItem[i].singleRowList[j].value}",
                    x,
                    y,
                    title
                )
            }
            y += 16F
        }

    }

    pdfDocument.finishPage(myPage)

    // below line is used to set the name of
    // our PDF file and its path.

    val strDate: String =
        SimpleDateFormat("dd-MM-yyyy HH-mm-ss", Locale.getDefault()).format(Date())
    val root = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        Constant.EXPORTED_DIRECTORY_PDF
    )
    if (!root.exists()) root.mkdirs()
    val path = File(root, "/$strDate-$titlePdf.${Constant.PDF_EXTENSION}")

    try {
        // after creating a file name we will
        // write our PDF file to that location.
        withContext(Dispatchers.IO) {
            pdfDocument.writeTo(FileOutputStream(path))
        }
        createdFile = path

        // on below line we are displaying a toast message as PDF file generated..
        Toast.makeText(applicationContext, "PDF file generated..", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        // below line is used
        // to handle error
        e.printStackTrace()

        // on below line we are displaying a toast message as fail to generate PDF
        Toast.makeText(applicationContext, "Fail to generate PDF file..", Toast.LENGTH_SHORT)
            .show()
    }
    // after storing our pdf to that
    // location we are closing our PDF file.
    pdfDocument.close()
    return createdFile
}