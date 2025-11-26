package cn.com.mustache.plugin.schemalens.ui

import cn.com.mustache.plugin.schemalens.model.ForeignKeyStructure
import javax.swing.table.AbstractTableModel

/**
 * Foreign key table model
 */
class ForeignKeyTableModel(
    private var rows: List<ForeignKeyStructure>
) : AbstractTableModel() {

    private val headers =
        listOf("", "Name", "Columns", "Referenced Table", "Referenced Columns", "On Update", "On Delete", "Comment")

    override fun getRowCount(): Int = rows.size

    override fun getColumnCount(): Int = headers.size

    override fun getColumnName(column: Int): String = headers[column]

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val row = rows[rowIndex]
        return when (columnIndex) {
            0 -> row.position
            1 -> row.name
            2 -> row.columns
            3 -> row.referencedTable
            4 -> row.referencedColumns
            5 -> row.onUpdate
            6 -> row.onDelete
            7 -> row.comment
            else -> ""
        }
    }

}

