package com.example.assetmanagement.data.roomModel

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.assetmanagement.data.model.ListItems
import com.example.assetmanagement.data.model.SingleRow

@Entity(tableName = "m_excel")
data class RoomExcelModel(
    @PrimaryKey(autoGenerate = true)
    var Id: Int,
    var fileName: String,
    var rowSequence: Int,
    var name: String,
    var value: String,
) {
    companion object {
        fun toRoomExcelModel(
            fileName: String,
            rowSequence: Int,
            singleRow: SingleRow
        ): RoomExcelModel {
            return RoomExcelModel(
                singleRow.id?:0,
                fileName,
                rowSequence,
                singleRow.name,
                singleRow.value.orEmpty()
            )
        }

        private fun toSingleRow(roomExcelModel: RoomExcelModel): SingleRow {
            return SingleRow(roomExcelModel.Id, roomExcelModel.name, roomExcelModel.value)
        }

        fun toListItems(listRoomExcelModel: List<RoomExcelModel>): ListItems {
            var listt = arrayListOf<SingleRow>()
            listRoomExcelModel.map {
                listt.add(toSingleRow(it))
            }
            return ListItems()
        }
    }
}