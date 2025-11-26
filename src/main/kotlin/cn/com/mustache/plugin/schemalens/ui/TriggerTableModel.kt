package cn.com.mustache.plugin.schemalens.ui

import cn.com.mustache.plugin.schemalens.model.TriggerStructure
import javax.swing.table.AbstractTableModel

/**
 * Trigger table model
 */
class TriggerTableModel(
    private var rows: List<TriggerStructure>
) : AbstractTableModel() {

    private val headers = listOf("","Name", "Event", "Timing", "Statement", "Comment")

    override fun getRowCount(): Int = rows.size

    override fun getColumnCount(): Int = headers.size

    override fun getColumnName(column: Int): String = headers[column]

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val row = rows[rowIndex]
        return when (columnIndex) {
            0 -> row.position
            1 -> row.name
            2 -> row.event
            3 -> row.timing
            4 -> row.statement
            5 -> row.comment
            else -> ""
        }
    }

}

