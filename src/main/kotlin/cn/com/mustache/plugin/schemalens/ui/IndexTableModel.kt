package cn.com.mustache.plugin.schemalens.ui

import cn.com.mustache.plugin.schemalens.model.IndexStructure
import javax.swing.table.AbstractTableModel

/**
 * Index table model
 */
class IndexTableModel(
    private var rows: List<IndexStructure>
) : AbstractTableModel() {

    private val headers = listOf("", "Name", "Columns", "Type", "Unique", "Comment")

    override fun getRowCount(): Int = rows.size

    override fun getColumnCount(): Int = headers.size

    override fun getColumnName(column: Int): String = headers[column]

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val row = rows[rowIndex]
        return when (columnIndex) {
            0 -> row.position
            1 -> row.name
            2 -> row.columns
            3 -> row.type
            4 -> formatBoolean(row.unique)
            5 -> row.comment
            else -> ""
        }
    }

    private fun formatBoolean(value: Boolean): String = if (value) "Yes" else "No"
}

