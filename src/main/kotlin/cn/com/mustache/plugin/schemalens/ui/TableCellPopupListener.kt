package cn.com.mustache.plugin.schemalens.ui

import cn.com.mustache.plugin.schemalens.framework.TableStructureConstants
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.awt.RelativePoint
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.JTextArea

/**
 * Table cell double-click listener to popup and view full content
 * Only triggers on comment fields or fields that can be very long
 */
class TableCellPopupListener : MouseAdapter() {

    private val longTextThreshold = TableStructureConstants.LONG_TEXT_THRESHOLD

    // Column names that should support popup (comment fields or fields that can be very long)
    private val popupEnabledColumns = setOf(
        "Comment",
        "Statement",
        "Expression",
        "Columns",
        "Referenced Columns"
    )

    override fun mouseClicked(e: MouseEvent) {
        if (e.clickCount == TableStructureConstants.DOUBLE_CLICK_COUNT) { // Double-click
            val table = e.source as? JTable ?: return
            val row = table.rowAtPoint(e.point)
            val column = table.columnAtPoint(e.point)

            if (row >= 0 && column >= 0) {
                // Check if this column supports popup
                val columnName = table.model.getColumnName(column)
                if (!popupEnabledColumns.contains(columnName)) {
                    return
                }

                val value = table.getValueAt(row, column)?.toString() ?: ""

                // If text is long, show popup window
                if (value.length > longTextThreshold && value.isNotEmpty()) {
                    showPopup(table, value, e)
                }
            }
        }
    }

    private fun showPopup(table: JTable, text: String, e: MouseEvent) {
        val textArea = createPopupTextArea(table, text)
        val calculatedSize = calculateOptimalSize(textArea, text)
        val scrollPane = createPopupScrollPane(textArea, calculatedSize)
        val popup = createPopup(scrollPane)

        // Ensure popup is disposed when closed to prevent memory leaks
        popup.addListener(object : com.intellij.openapi.ui.popup.JBPopupListener {
            override fun onClosed(event: com.intellij.openapi.ui.popup.LightweightWindowEvent) {
                // Clean up resources
                textArea.text = ""
                scrollPane.viewport?.remove(textArea)
            }
        })

        val point = RelativePoint(e)
        popup.show(point)
    }

    /**
     * Calculate optimal popup size based on text content
     */
    private fun calculateOptimalSize(textArea: JTextArea, text: String): java.awt.Dimension {
        val fontMetrics = textArea.getFontMetrics(textArea.font)
        val charWidth = fontMetrics.charWidth('M') // Use 'M' as average character width
        val lineHeight = fontMetrics.height

        // Calculate width based on text length and line breaks
        val lines = text.split('\n')
        val maxLineLength = lines.maxOfOrNull { it.length } ?: 0

        // Calculate width: consider character width and add some padding
        // For very long lines, use a reasonable default width
        val calculatedWidth = if (maxLineLength > 100) {
            // For very long lines, use a fixed reasonable width
            TableStructureConstants.POPUP_DEFAULT_WIDTH
        } else {
            // For shorter lines, calculate based on content
            (maxLineLength * charWidth * 1.3).toInt() // Add 30% padding
        }

        // Clamp width between min and max
        val width = calculatedWidth
            .coerceAtLeast(TableStructureConstants.POPUP_MIN_WIDTH)
            .coerceAtMost(TableStructureConstants.POPUP_MAX_WIDTH)

        // Calculate height based on number of lines and word wrapping
        // Set a temporary width to help calculate wrapped lines
        val availableTextWidth = width - TableStructureConstants.POPUP_BORDER_SIZE * 2 - 20 // Account for scrollbar

        var totalVisualLines = 0
        for (line in lines) {
            if (line.isEmpty()) {
                totalVisualLines += 1 // Empty line still takes one line
            } else {
                val lineWidth = fontMetrics.stringWidth(line)
                // Calculate how many visual lines this line will take
                val visualLines = if (lineWidth > availableTextWidth && availableTextWidth > 0) {
                    // Line will wrap, estimate number of wrapped lines
                    val estimatedCharsPerLine = (availableTextWidth / charWidth).coerceAtLeast(10)
                    (line.length / estimatedCharsPerLine).coerceAtLeast(1) + 1
                } else {
                    1
                }
                totalVisualLines += visualLines
            }
        }

        // Add some padding lines for better readability
        totalVisualLines += 3

        // Calculate height with line spacing and borders
        val calculatedHeight =
            (totalVisualLines * lineHeight * TableStructureConstants.POPUP_LINE_HEIGHT_MULTIPLIER).toInt() +
                    TableStructureConstants.POPUP_BORDER_SIZE * 2

        // Clamp height between min and max
        val height = calculatedHeight
            .coerceAtLeast(TableStructureConstants.POPUP_MIN_HEIGHT)
            .coerceAtMost(TableStructureConstants.POPUP_MAX_HEIGHT)

        return java.awt.Dimension(width, height)
    }

    private fun createPopupTextArea(table: JTable, text: String): JTextArea {
        return JTextArea(text).apply {
            isEditable = false
            isOpaque = true
            lineWrap = true
            wrapStyleWord = true
            font = table.font.deriveFont(java.awt.Font.PLAIN, TableStructureConstants.DEFAULT_FONT_SIZE)
            background = table.background
            foreground = table.foreground
            border = javax.swing.BorderFactory.createEmptyBorder(
                TableStructureConstants.POPUP_BORDER_SIZE,
                TableStructureConstants.POPUP_BORDER_SIZE,
                TableStructureConstants.POPUP_BORDER_SIZE,
                TableStructureConstants.POPUP_BORDER_SIZE
            )
            caretPosition = TableStructureConstants.CARET_POSITION_TOP // Scroll to top
        }
    }

    private fun createPopupScrollPane(textArea: JTextArea, size: java.awt.Dimension): JScrollPane {
        return JScrollPane(textArea).apply {
            preferredSize = size
            minimumSize = java.awt.Dimension(
                TableStructureConstants.POPUP_MIN_WIDTH,
                TableStructureConstants.POPUP_MIN_HEIGHT
            )
            maximumSize = java.awt.Dimension(
                TableStructureConstants.POPUP_MAX_WIDTH,
                TableStructureConstants.POPUP_MAX_HEIGHT
            )
            border = javax.swing.BorderFactory.createEmptyBorder()
        }
    }

    private fun createPopup(scrollPane: JScrollPane): com.intellij.openapi.ui.popup.JBPopup {
        return JBPopupFactory.getInstance().createComponentPopupBuilder(scrollPane, null)
            .setTitle(TableStructureConstants.POPUP_TITLE_VIEW_FULL_CONTENT)
            .setResizable(true)
            .setMovable(true)
            .setRequestFocus(true)
            .setCancelOnClickOutside(true)
            .createPopup()
    }
}

