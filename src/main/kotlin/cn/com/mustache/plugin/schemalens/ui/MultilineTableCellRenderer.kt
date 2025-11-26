package cn.com.mustache.plugin.schemalens.ui

import cn.com.mustache.plugin.schemalens.framework.TableStructureConstants
import java.awt.Component
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellRenderer

/**
 * Multiline text cell renderer - supports long text display and tooltips.
 *
 * This renderer provides:
 * - Text truncation for long values (with ellipsis)
 * - HTML-formatted tooltips with word wrapping
 * - Multi-line text support in tooltips
 *
 * The renderer automatically truncates text longer than [TableStructureConstants.MAX_DISPLAY_LENGTH]
 * and shows full content in tooltips for text longer than [TableStructureConstants.TOOLTIP_THRESHOLD].
 *
 * @see TableStructureConstants.MAX_DISPLAY_LENGTH
 * @see TableStructureConstants.TOOLTIP_THRESHOLD
 */
class MultilineTableCellRenderer : DefaultTableCellRenderer(), TableCellRenderer {

    private val maxDisplayLength = TableStructureConstants.MAX_DISPLAY_LENGTH
    private val tooltipThreshold = TableStructureConstants.TOOLTIP_THRESHOLD

    override fun getTableCellRendererComponent(
        table: JTable?,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        val text = value?.toString() ?: ""

        // If text exceeds maximum display length, truncate and add ellipsis
        // Optimized: use StringBuilder for string concatenation
        val displayText = if (text.length > maxDisplayLength) {
            StringBuilder(maxDisplayLength + TableStructureConstants.ELLIPSIS.length)
                .append(text, 0, maxDisplayLength)
                .append(TableStructureConstants.ELLIPSIS)
                .toString()
        } else {
            text
        }

        // Set display text
        super.getTableCellRendererComponent(table, displayText, isSelected, hasFocus, row, column)

        // Set tooltip - if text is long, show full content (HTML format, supports multi-line)
        if (text.length > tooltipThreshold && text.isNotEmpty()) {
            toolTipText = formatTooltip(text)
        } else {
            toolTipText = null
        }

        return this
    }

    /**
     * Format tooltip text, supports multi-line display
     */
    private fun formatTooltip(text: String): String {
        // Escape HTML special characters (optimized with StringBuilder)
        val escapedText = escapeHtml(text)

        // If text contains line breaks, use directly (optimized: use StringBuilder for replace)
        if (text.contains('\n')) {
            val replacedText = escapedText.replace("\n", TableStructureConstants.HTML_BREAK)
            return buildTooltipHtml(replacedText)
        }

        // For long text, wrap every 80 characters, but keep words intact
        val wrappedText = wrapText(escapedText, TableStructureConstants.TOOLTIP_WRAP_LENGTH)
        return buildTooltipHtml(wrappedText)
    }

    /**
     * Escape HTML special characters using StringBuilder for better performance
     */
    private fun escapeHtml(text: String): String {
        if (text.isEmpty()) return text

        val sb = StringBuilder(text.length)
        for (char in text) {
            when (char) {
                '&' -> sb.append("&amp;")
                '<' -> sb.append("&lt;")
                '>' -> sb.append("&gt;")
                '"' -> sb.append("&quot;")
                '\'' -> sb.append("&#39;")
                else -> sb.append(char)
            }
        }
        return sb.toString()
    }

    /**
     * Build HTML tooltip string
     */
    private fun buildTooltipHtml(content: String): String {
        return "<html><body style='width: ${TableStructureConstants.TOOLTIP_WIDTH}px; white-space: pre-wrap;'>$content</body></html>"
    }

    /**
     * Smart text wrapping, try to wrap at word boundaries
     * Optimized: avoid split when possible, use StringBuilder for better performance
     */
    private fun wrapText(text: String, maxLineLength: Int): String {
        if (text.length <= maxLineLength) {
            return text
        }

        val result =
            StringBuilder(text.length + text.length / maxLineLength * TableStructureConstants.HTML_BREAK.length)
        var currentLine = StringBuilder(maxLineLength)
        var wordStart = 0

        // Process text character by character to avoid split overhead
        for (i in text.indices) {
            val char = text[i]

            if (char == ' ' || i == text.length - 1) {
                // End of word (or end of text)
                val wordEnd = if (i == text.length - 1) i + 1 else i
                val word = text.substring(wordStart, wordEnd)

                if (currentLine.isEmpty()) {
                    currentLine.append(word)
                } else if (currentLine.length + word.length + 1 <= maxLineLength) {
                    currentLine.append(TableStructureConstants.SPACE).append(word)
                } else {
                    // Add current line to result and start new line
                    if (result.isNotEmpty()) {
                        result.append(TableStructureConstants.HTML_BREAK)
                    }
                    result.append(currentLine)
                    currentLine = StringBuilder(word)
                }

                wordStart = i + 1
            }
        }

        // Add remaining line
        if (currentLine.isNotEmpty()) {
            if (result.isNotEmpty()) {
                result.append(TableStructureConstants.HTML_BREAK)
            }
            result.append(currentLine)
        }

        return result.toString()
    }
}

