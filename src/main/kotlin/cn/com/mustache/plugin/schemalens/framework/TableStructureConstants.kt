package cn.com.mustache.plugin.schemalens.framework

import cn.com.mustache.plugin.schemalens.model.TableStructureData
import com.intellij.openapi.util.Key
import com.intellij.ui.JBColor
import java.awt.Color

/**
 * Table structure related constants
 */
object TableStructureConstants {
    /**
     * Key for storing table data
     */
    val TABLE_DATA_KEY: Key<TableStructureData> = Key.create("table.structure.data")

    // Column widths for different tables
    val STRUCTURE_COLUMN_WIDTHS = intArrayOf(35, 150, 100, 90, 150, 100, 100, 250)
    val INDEX_COLUMN_WIDTHS = intArrayOf(35, 200, 300, 100, 80, 250)
    val FOREIGN_KEY_COLUMN_WIDTHS = intArrayOf(35, 150, 150, 150, 150, 100, 100, 200)
    val TRIGGER_COLUMN_WIDTHS = intArrayOf(35, 150, 100, 100, 250, 200)
    val CHECK_COLUMN_WIDTHS = intArrayOf(35, 200, 500, 250)

    // Text display constants
    const val MAX_DISPLAY_LENGTH = 80
    const val TOOLTIP_THRESHOLD = 30
    const val LONG_TEXT_THRESHOLD = 10
    const val TOOLTIP_WIDTH = 500
    const val TOOLTIP_WRAP_LENGTH = 80
    const val ELLIPSIS = "..."

    // Adaptive popup size constants
    const val POPUP_MIN_WIDTH = 400
    const val POPUP_MAX_WIDTH = 1000
    const val POPUP_MIN_HEIGHT = 200
    const val POPUP_MAX_HEIGHT = 800
    const val POPUP_DEFAULT_WIDTH = 600
    const val POPUP_LINE_HEIGHT_MULTIPLIER = 1.2 // Line height multiplier for text area
    const val POPUP_BORDER_SIZE = 10
    const val DEFAULT_FONT_SIZE = 13f
    const val MONOSPACED_FONT_SIZE = 13
    val LIGHT_MODE_BACKGROUND = JBColor(Color(0xf5f5f5), Color(0x2b2b2b))
    val LIGHT_MODE_FOREGROUND = JBColor(Color(0x000000), Color(0xffffff))

    // Tab names
    const val TAB_STRUCTURE = "Structure"
    const val TAB_INDEX = "Index"
    const val TAB_FOREIGN_KEY = "Foreign Key"
    const val TAB_TRIGGER = "Trigger"
    const val TAB_CHECK = "Check"
    const val TAB_COMMENT = "Comment"
    const val TAB_SQL_PREVIEW = "SQL Preview"

    // Messages
    const val MSG_NO_COMMENT = "(No comment)"
    const val MSG_UNABLE_TO_GET_SQL = "(Unable to get CREATE TABLE statement)"
    const val MSG_UNABLE_TO_GET_SQL_WITH_ERROR = "(Unable to get CREATE TABLE statement: %s)"
    const val POPUP_TITLE_VIEW_FULL_CONTENT = "View Full Content"
    const val MENU_COPY = "Copy"
    const val MENU_SELECT_ALL = "Select All"
    const val EDITOR_NAME = "Structure"

    // Index type constants
    const val INDEX_TYPE_PRIMARY = "PRIMARY"
    const val INDEX_TYPE_UNIQUE = "UNIQUE"
    const val INDEX_TYPE_INDEX = "INDEX"
    const val INDEX_PREFIX_PRIMARY = "PK_"
    const val INDEX_NAME_PRIMARY = "PRIMARY"

    // Foreign key constants
    const val FK_PREFIX = "FK_"

    // String operations
    const val COLUMN_SEPARATOR = ", "
    const val HTML_BREAK = "<br>"
    const val SPACE = " "
    const val DOUBLE_CLICK_COUNT = 2
    const val CARET_POSITION_TOP = 0

    const val REFLECTION_CLASS_DATABASE_CONTEXT_FUN = "com.intellij.database.view.DatabaseContextFun"
    const val REFLECTION_METHOD_GET_SELECTED_DB_ELEMENTS = "getSelectedDbElements"
}

