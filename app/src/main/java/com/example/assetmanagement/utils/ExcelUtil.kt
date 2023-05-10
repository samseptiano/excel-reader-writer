package com.example.assetmanagement.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.assetmanagement.R
import com.example.assetmanagement.data.model.ListItems
import com.example.assetmanagement.data.model.SingleRow
import com.example.assetmanagement.utils.Constant.Companion.DOC
import com.example.assetmanagement.utils.Constant.Companion.EXPORTED_DIRECTORY
import com.example.assetmanagement.utils.Constant.Companion.XLSX_EXTENSION
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.apache.poi.EncryptedDocumentException
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.crypt.Decryptor
import org.apache.poi.poifs.crypt.EncryptionInfo
import org.apache.poi.poifs.filesystem.OfficeXmlFileException
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * Created by samuel.septiano on 03/05/2023.
 */

fun Context.createXlsx(title: String, modelHistoryAbsen: List<ListItems>): File? {

    try {
        val strDate: String =
            SimpleDateFormat("dd-MM-yyyy HH-mm-ss", Locale.getDefault()).format(Date())
        val root = File(
            Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            EXPORTED_DIRECTORY
        )
        if (!root.exists()) root.mkdirs()
        val path = File(root, "/$strDate - $title.$XLSX_EXTENSION")
        val workbook = XSSFWorkbook()
        val outputStream = FileOutputStream(path)
        val headerStyle = workbook.createCellStyle()
        headerStyle.setAlignment(HorizontalAlignment.CENTER)
        headerStyle.fillForegroundColor = IndexedColors.BLUE_GREY.getIndex()
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
        headerStyle.setBorderTop(BorderStyle.MEDIUM)
        headerStyle.setBorderBottom(BorderStyle.MEDIUM)
        headerStyle.setBorderRight(BorderStyle.MEDIUM)
        headerStyle.setBorderLeft(BorderStyle.MEDIUM)
        val font = workbook.createFont()
        font.fontHeightInPoints = 12.toShort()
        font.color = IndexedColors.WHITE.getIndex()
        font.bold = true
        headerStyle.setFont(font)
        val sheet = workbook.createSheet(title.uppercase())
        var row = sheet.createRow(0)
        var cell: XSSFCell?

        //buat judul header
        for (i in modelHistoryAbsen[0].singleRowList.indices) {
            cell = row.createCell(i)
            cell.setCellValue(modelHistoryAbsen[0].singleRowList[i].name)
            cell.cellStyle = headerStyle
        }

        //buat isi
        for (i in modelHistoryAbsen.indices) {
            row = sheet.createRow(i + 1)
            for (j in modelHistoryAbsen[i].singleRowList.indices) {
                cell = row.createCell(j)
                cell.setCellValue(modelHistoryAbsen[i].singleRowList[j].value)
                sheet.setColumnWidth(
                    0,
                    (modelHistoryAbsen[i].singleRowList[j].value?.length?.plus(30))?.times(256) ?: 0
                )
            }

        }
        workbook.write(outputStream)
        outputStream.close()
        Toast.makeText(this, getString(R.string.data_exported_successfully), Toast.LENGTH_SHORT)
            .show()
        return path
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
}

fun Context.getFileName(uri: Uri): String? = when (uri.scheme) {
    ContentResolver.SCHEME_CONTENT -> this.getContentFileName(uri)
    else -> uri.path?.let(::File)?.name
}

fun Context.getContentFileName(uri: Uri): String? = runCatching {
    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        cursor.moveToFirst()
        return@use cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
            .let(cursor::getString)
    }
}.getOrNull()

fun Uri.getExtension(context: Context): String? {
    var extension: String? = ""
    extension = if (this.scheme == ContentResolver.SCHEME_CONTENT) {
        val mime = MimeTypeMap.getSingleton()
        mime.getExtensionFromMimeType(context.contentResolver.getType(this))
    } else {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            MimeTypeMap.getFileExtensionFromUrl(
                FileProvider.getUriForFile(
                    context,
                    context.packageName + ".provider",
                    File(this.path.toString())
                )
                    .toString()
            )
        } else {
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(this.path.toString())).toString())
        }
    }
    return extension
}


fun Context.copyFileAndExtract(
    uri: Uri,
    extension: String,
    onLoading: () -> Unit,
    onFinishLoading: () -> Unit,
    callBackAction: (File?, Uri?) -> Unit
) {
    var file: File?
    var fileUri: Uri?

    onLoading()
    val dir = File(this.filesDir, DOC)
    dir.mkdirs()
    val fileName = getFileName(uri)
    file = File(dir, fileName.toString())
    file.createNewFile()
    val fout = FileOutputStream(file)
    try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            fout.use { output ->
                inputStream.copyTo(output)
                output.flush()
            }
        }
        fileUri = FileProvider.getUriForFile(this, "$packageName.provider", file)
    } catch (e: Exception) {
        onFinishLoading()
        fileUri = uri
        e.printStackTrace()
    }
    callBackAction(file, fileUri)
}

fun Context.readExcelFileFromAssets(
    coroutineScope: CoroutineScope,
    filePath: String,
    password: String = "",
    onCallbackError: (String) -> Unit,
    onCallbackResult: (List<ListItems>, MutableList<String>) -> Unit
) : String{
    var file: File? = null
    var workbook: Workbook
    val list = ArrayList<ListItems>()
    var filename = ""
    list.clear()
    file = File(filePath)
    try {
        if (!file.exists() || file.length() == 0L) {
            onCallbackError(getString(R.string.err_invalid_file_selected))
        }
        if (file.length() > Int.MAX_VALUE) {
            onCallbackError(getString(R.string.err_file_too_big))
        }
        coroutineScope.launch {
            val myInput = FileInputStream(file)
            val firstRow: MutableList<String> = arrayListOf()
            if (password.isNotEmpty()) {
                workbook = WorkbookFactory.create(file, password)
                val posFile = POIFSFileSystem(file, true)
                filename = file.name
                workbook = if (file.name.endsWith(XLSX_EXTENSION)) {
                    val info = EncryptionInfo(posFile)
                    val d = Decryptor.getInstance((info))
                    if (!d.verifyPassword(password)) {
                        onCallbackError(getString(R.string.err_password_incorrect))
                        return@launch
                    }
                    XSSFWorkbook(d.getDataStream(posFile))
                } else {
                    org.apache.poi.hssf.record.crypto.Biff8EncryptionKey.setCurrentUserPassword(
                        password
                    )
                    HSSFWorkbook(posFile.root, true)
                }
            } else {
                filename = file.name

                if (file.name.endsWith(XLSX_EXTENSION)) {
                    workbook = XSSFWorkbook(myInput)
                } else {
                    workbook = HSSFWorkbook(myInput)
                }
            }

            //tambah status complete dan pending
            //workbook = addColumnIfNotAdded(workbook)

            val mySheet = workbook.getSheetAt(0)
            val rowIter: Iterator<Row> = mySheet.iterator()
            while (rowIter.hasNext()) {
                val row: Row = rowIter.next()
                val cellIter1: Iterator<Cell> = row.cellIterator()
                if (row.rowNum == 0) {
                    while (cellIter1.hasNext()) {
                        val firstCell: Cell = cellIter1.next()
                        firstRow.add(firstCell.toString())
                    }
                }
                val cellIter: Iterator<Cell> = row.cellIterator()
                val singleRowList: ArrayList<SingleRow> = arrayListOf()
                if (row.rowNum > 0) {
                    while (cellIter.hasNext()) {
                        for (i in firstRow) {
                            if (cellIter.hasNext()) {
                                val cell: Cell = cellIter.next()
                                singleRowList.add(SingleRow(null, i.toString(), cell.toString()))
                            }
                        }
                    }
//                    if (singleRowList.isNotEmpty()) {
//                        try {
//                            if (singleRowList[singleRowList.size - 1].value?.isNotEmpty() == true) {
//                                if (singleRowList[singleRowList.size - 1].value?.equals(
//                                        Constant.FLAG_COMPLETE
//                                    ) == false
//                                ) {
//                                    singleRowList[singleRowList.size - 1].value =
//                                        Constant.FLAG_PENDING
//                                }
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }
                    list.add(ListItems(singleRowList))
                }
            }
            onCallbackResult(list, firstRow)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        onCallbackError(e.message.orEmpty())
    }
    return filename
}

private fun addColumnIfNotAdded(workBook: Workbook): Workbook {
    val sheet = workBook.getSheetAt(0)
    val rowIterator: Iterator<Row> = sheet.iterator()
    while (rowIterator.hasNext()) {
        val row: Row = rowIterator.next()
        val cellIterator: Iterator<Cell> = row.cellIterator()
        while (cellIterator.hasNext()) {
            val column: Cell = cellIterator.next()
            if (!cellIterator.hasNext()) {
                if (column.cellType == Cell.CELL_TYPE_STRING) {
                    if (column.stringCellValue.equals(Constant.STATUS) ||
                        column.stringCellValue.equals(Constant.FLAG_COMPLETE)
                    ) {
                        if (column.stringCellValue.equals(Constant.FLAG_COMPLETE)) {
                            val style = workBook.createCellStyle()
                            style.fillBackgroundColor = IndexedColors.YELLOW.index
                            style.setFillPattern(FillPatternType.SOLID_FOREGROUND)
                            row.rowStyle = style
                        }
                    } else {
                        val cell = row.createCell(row.lastCellNum + 1)
                        cell.setCellValue(Constant.STATUS)
                    }
                } else {
                    val cell = row.createCell(row.lastCellNum + 1)
                    cell.setCellValue(Constant.STATUS)
                }
            }
        }
    }
    return workBook
}

fun String.isFileEncrypt(): Boolean {
    val file = File(this)
    val myInput = FileInputStream(file)
    val workbook: Workbook
    try {
        if (file.name.contains(XLSX_EXTENSION)) {
            return try {
                try {
                    POIFSFileSystem(myInput)
                } catch (_: IOException) {
                }
                true
            } catch (e: OfficeXmlFileException) {
                false
            }
        } else {
            workbook = HSSFWorkbook(myInput)
        }
    } catch (e: EncryptedDocumentException) {
        return true
    }
    return false
}


