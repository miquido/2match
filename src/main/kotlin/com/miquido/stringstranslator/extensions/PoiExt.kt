package com.miquido.stringstranslator.extensions

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet

fun Row?.isEmpty(): Boolean {
    if (this == null || lastCellNum <= 0) {
        return true
    }
    (firstCellNum until lastCellNum)
            .map { getCell(it) }
            .forEach {
                if (it != null && it.cellType !== CellType.BLANK && it.toString().isNotBlank()) {
                    return false
                }
            }
    return true
}

fun Sheet.createNewRowOrUseExisting(currentRowIndex: Int): Row =
        this.getRow(currentRowIndex) ?: this.createRow(currentRowIndex)

fun Row.createNewCellOrUseExisting(currentCellIndex: Int): Cell =
        this.getCell(currentCellIndex) ?: this.createCell(currentCellIndex)

fun Sheet.copyValuesTo(singleStringsSheet: Sheet) {
    for (rowIndex in 0..lastRowNum) {
        val sourceRow = getRow(rowIndex)
        val destRow = singleStringsSheet.createRow(rowIndex)
        if (!sourceRow.isEmpty()) {
            for (colIndex in 0..sourceRow.lastCellNum) {
                val destCell = destRow.createCell(colIndex)
                val sourceCell = sourceRow.getCell(colIndex)
                if (sourceCell != null) {
                    destCell.setCellValue(sourceCell.stringCellValue)
                } else {
                    destCell.setCellValue("")
                }
            }
        }
    }
}