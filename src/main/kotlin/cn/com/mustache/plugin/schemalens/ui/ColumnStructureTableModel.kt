package cn.com.mustache.plugin.schemalens.ui

import cn.com.mustache.plugin.schemalens.model.ColumnStructure
import javax.swing.table.AbstractTableModel

/**
 * Column table model
 */
class ColumnStructureTableModel(
    private var rows: List<ColumnStructure>
) : AbstractTableModel() {

    private val headers = listOf(" ", "Column", "Type", "Nullable", "Default", "Primary Key", "Auto Increment", "Comment")

    override fun getRowCount(): Int = rows.size

    override fun getColumnCount(): Int = headers.size

    override fun getColumnName(column: Int): String = headers[column]

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val row = rows[rowIndex]
        return when (columnIndex) {
            0 -> row.position
            1 -> row.name
            2 -> row.dataType
            3 -> formatBoolean(row.nullable)
            4 -> row.defaultValue ?: ""
            5 -> formatBoolean(row.primaryKey)
            6 -> formatBoolean(row.autoIncrement)
            7 -> row.comment
            else -> ""
        }
    }

    private fun formatBoolean(value: Boolean): String = if (value) "Yes" else "No"
}

