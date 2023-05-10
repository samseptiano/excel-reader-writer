package com.example.assetmanagement.ui.viewmodel


import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.assetmanagement.data.model.ListItems
import com.example.assetmanagement.utils.readExcelFileFromAssets
import com.example.assetmanagement.base.viewmodel.BaseViewModel
import com.example.assetmanagement.data.roomModel.RoomExcelModel
import com.example.assetmanagement.data.roomModel.RoomExcelModel.Companion.toListItems
import com.example.assetmanagement.domain.usecase.excelUseCase.DeleteItemUseCase
import com.example.assetmanagement.domain.usecase.excelUseCase.GetAllItemUseCase
import com.example.assetmanagement.domain.usecase.excelUseCase.InsertItemUseCase
import com.example.assetmanagement.domain.usecase.excelUseCase.UpdateItemUseCase
import com.example.assetmanagement.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.EncryptedDocumentException
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.OfficeXmlFileException
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class ExcellReaderViewModel @Inject constructor(
    private val insertItemUseCase: InsertItemUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val getAllItemUseCase: GetAllItemUseCase
) : BaseViewModel() {
    var excelDataListLiveData: MutableLiveData<List<ListItems>> = MutableLiveData()
    var excelExceptionListData: MutableLiveData<String> = MutableLiveData()
    var excellFirstRowData: MutableLiveData<List<String>> = MutableLiveData()

    private val list = ArrayList<ListItems>()
    lateinit var workbook: Workbook
    lateinit var file: File
    lateinit var fileDir: File
    var filename = ""

    fun readExcelFileFromAssets(context: Context, filePath: String, password: String = "") {
        list.clear()
        file = File(filePath)
        filename = file.name
        context.readExcelFileFromAssets(coroutineScope, filePath, password, {
            excelExceptionListData.postValue(it)
        }, { lists, firstRow ->
            list.addAll(lists)
            excelDataListLiveData.postValue(list)
            excellFirstRowData.postValue(firstRow)

            viewModelScope.launch(Dispatchers.IO) {
                deleteLocal(null, null)
            }

        })
    }
    fun sortBy(sortBy: String) {
        launch {
            try {
                val filtered = list
                    .map { excelData ->
                        excelData.apply {
                            this.filterItem = this.singleRowList.find {
                                it.name == sortBy
                            }?.value?.lowercase()
                        }
                    }.sortedWith(compareBy{ it.filterItem })
                excelDataListLiveData.postValue(filtered)
            } catch (e: Exception) {
                e.printStackTrace()
                excelDataListLiveData.postValue(list)
            }
        }
    }

    fun isEncrypt(filepath: String): Boolean {
        val file = File(filepath)
        val myInput = FileInputStream(file)
        val workbook: Workbook
        try {
            if (file.name.contains("xlsx")) {
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

    fun checkPassword(password: String, path: String): Boolean {
        try {
            val file = File(path)
            val wb = WorkbookFactory.create(
                file,
                password
            )
        } catch (e: EncryptedDocumentException) {
            return false
        }
        return true
    }

    fun setUpdateDataCell(cellNum: Int, rowNum: Int, value: String) {
        if (::workbook.isInitialized) {
            val sheet = workbook.getSheetAt(0)
            val row = sheet.getRow(rowNum + 1)
            Log.e("Last count : ", row.getCell(cellNum).toString())
            val cell = row.getCell(cellNum)
            if (cell != null) {
                cell.setCellValue(value)
                val newFile = File(fileDir, file.name.orEmpty())
                if (file.exists()) {
                    file.delete()
                }
                newFile.createNewFile()
                val fout = FileOutputStream(newFile)
                workbook.write(fout)
                fout.close()

                Log.e("file saving", "File Save Successfully")
            } else {
                Log.e("Cell", "null")
            }
        }
    }

    fun setPositionCompleted(rowNum: Int) {
        if (::workbook.isInitialized) {
            val sheet = workbook.getSheetAt(0)
            val row = sheet.getRow(rowNum + 1)
            Log.e("Last count : ", row.lastCellNum.toString())
            val cell = row.getCell(row.lastCellNum.toInt() - 1)
            if (cell != null) {
                Log.e("File Save Cell : ", cell.stringCellValue.toString())
                val status = cell.stringCellValue.toString()
                if (status.contentEquals(Constant.FLAG_COMPLETE)) {
                    cell.setCellValue(Constant.STATUS)
                } else {
                    cell.setCellValue(Constant.FLAG_COMPLETE)
                }
                val newFile = File(fileDir, file.name.orEmpty())
                if (file.exists()) {
                    file.delete()
                }
                newFile.createNewFile()
                val fout = FileOutputStream(newFile)
                workbook.write(fout)
                fout.close()

                Log.e("file saving", "File Save Successfully")
            } else {
                Log.e("Cell", "null")
            }
        }
    }

    suspend fun saveToLocal(fileName: String, listItem: ArrayList<ListItems>) {
        val params = InsertItemUseCase.Params(fileName, listItem)
        insertItemUseCase.run(params)

    }

    suspend fun updateLocal(fileName: String, rowSequence: Int, listItem: ListItems) {
        for (i in 0 until listItem.singleRowList.size) {
            val params =
                UpdateItemUseCase.Params(fileName, rowSequence, listItem.singleRowList[i], "")
            updateItemUseCase.run(params)
        }
    }

    private suspend fun deleteLocal(fileName: String?, roomExcelModel: RoomExcelModel?) {
        val params = DeleteItemUseCase.Params(roomExcelModel?.Id, fileName)
        deleteItemUseCase.run(params)
    }

    suspend fun getLocal(cellId: Int?, fileName: String?) = withContext(Dispatchers.IO) {
        val params = GetAllItemUseCase.Params(cellId, fileName)
        val result = getAllItemUseCase.run(params)

        if (result.isNotEmpty()) {
            val groupedRow = result.groupBy { it.rowSequence }
            val groupSizes = groupedRow.size
            val firstRow = arrayListOf<String>()
            list.clear()
            for (i in 0 until groupSizes) {

                val listPerRow = groupedRow[i]?.let {
                    if (i == 0) {
                        it.map { roomExcelModel ->
                            firstRow.add(roomExcelModel.name)
                        }
                    }
                    toListItems(it)
                }
                if (listPerRow != null) {
                    list.add(listPerRow)
                }
            }

            excelDataListLiveData.postValue(list)
            excellFirstRowData.postValue(firstRow)
        }
    }
}
