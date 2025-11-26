package cn.com.mustache.plugin.schemalens.ui

import cn.com.mustache.plugin.schemalens.model.CheckStructure
import javax.swing.table.AbstractTableModel

/**
 * Check constraint table model
 */
class CheckTableModel(
    private var rows: List<CheckStructure>
) : AbstractTableModel() {

    private val headers = listOf("","Name", "Expression", "Comment")

    override fun getRowCount(): Int = rows.size

    override fun getColumnCount(): Int = headers.size

    override fun getColumnName(column: Int): String = headers[column]

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val row = rows[rowIndex]
        return when (columnIndex) {
            0 -> row.position
            1 -> row.name
            2 -> row.expression
            3 -> row.comment
            else -> ""
        }
    }

}

