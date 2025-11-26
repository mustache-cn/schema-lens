package cn.com.mustache.plugin.schemalens.ui

import cn.com.mustache.plugin.schemalens.framework.TableStructureConstants
import cn.com.mustache.plugin.schemalens.model.*
import com.intellij.database.psi.DbTable
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import com.intellij.ui.TableSpeedSearch
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.UIUtil
import java.awt.BorderLayout
import java.awt.Font
import java.beans.PropertyChangeListener
import javax.swing.*

/**
 * Table structure file editor - integrates all tabs
 */
class TableStructureFileEditor(
    private val file: VirtualFile,
    private val dbTable: DbTable,
    columns: List<ColumnStructure>,
    indexes: List<IndexStructure>,
    foreignKeys: List<ForeignKeyStructure>,
    triggers: List<TriggerStructure>,
    checks: List<CheckStructure>,
    private val tableComment: String
) : FileEditor {
    private val structureModel = ColumnStructureTableModel(columns)
    private val indexModel = IndexTableModel(indexes)
    private val foreignKeyModel = ForeignKeyTableModel(foreignKeys)
    private val triggerModel = TriggerTableModel(triggers)
    private val checkModel = CheckTableModel(checks)
    private val panel = JPanel(BorderLayout())

    // Track listeners and components for proper disposal
    private val mouseListeners = mutableListOf<TableCellPopupListener>()
    private val popupMenus = mutableListOf<JPopupMenu>()
    private val sqlTextArea: JTextArea = createSqlTextArea()
    private val sqlScrollPane: JBScrollPane = createSqlScrollPane(sqlTextArea)
    private val messageBusConnection = ApplicationManager.getApplication().messageBus.connect()

    init {
        applySqlEditorColors()
        messageBusConnection.subscribe(LafManagerListener.TOPIC, LafManagerListener {
            applySqlEditorColors()
        })

        val tabbedPane = JBTabbedPane().apply {
            // Set tab font to bold using UIManager
            val originalFont = UIManager.getFont("TabbedPane.font")
            if (originalFont != null) {
                UIManager.put("TabbedPane.font", originalFont.deriveFont(Font.BOLD))
            } else {
                // Fallback: set component font directly
                font = font.deriveFont(Font.BOLD)
            }
        }

        // Add all tabs
        addStructureTab(tabbedPane)
        addIndexTab(tabbedPane)
        addForeignKeyTab(tabbedPane)
        addTriggerTab(tabbedPane)
        addCheckTab(tabbedPane)
        addCommentTab(tabbedPane)
        addSqlPreviewTab(tabbedPane)

        panel.add(tabbedPane, BorderLayout.CENTER)
    }

    private fun addStructureTab(tabbedPane: JTabbedPane) {
        val table = createTableWithColumnWidths(structureModel, TableStructureConstants.STRUCTURE_COLUMN_WIDTHS)
        tabbedPane.addTab(TableStructureConstants.TAB_STRUCTURE, createTableTab(table))
    }

    private fun addIndexTab(tabbedPane: JTabbedPane) {
        val table = createTableWithColumnWidths(indexModel, TableStructureConstants.INDEX_COLUMN_WIDTHS)
        tabbedPane.addTab(TableStructureConstants.TAB_INDEX, createTableTab(table))
    }

    private fun addForeignKeyTab(tabbedPane: JTabbedPane) {
        val table = createTableWithColumnWidths(foreignKeyModel, TableStructureConstants.FOREIGN_KEY_COLUMN_WIDTHS)
        tabbedPane.addTab(TableStructureConstants.TAB_FOREIGN_KEY, createTableTab(table))
    }

    private fun addTriggerTab(tabbedPane: JTabbedPane) {
        val table = createTableWithColumnWidths(triggerModel, TableStructureConstants.TRIGGER_COLUMN_WIDTHS)
        tabbedPane.addTab(TableStructureConstants.TAB_TRIGGER, createTableTab(table))
    }

    private fun addCheckTab(tabbedPane: JTabbedPane) {
        val table = createTableWithColumnWidths(checkModel, TableStructureConstants.CHECK_COLUMN_WIDTHS)
        tabbedPane.addTab(TableStructureConstants.TAB_CHECK, createTableTab(table))
    }

    private fun addCommentTab(tabbedPane: JTabbedPane) {
        val commentTextArea = createCommentTextArea()
        tabbedPane.addTab(TableStructureConstants.TAB_COMMENT, JBScrollPane(commentTextArea))
    }

    private fun addSqlPreviewTab(tabbedPane: JTabbedPane) {
        tabbedPane.addTab(TableStructureConstants.TAB_SQL_PREVIEW, sqlScrollPane)
    }

    private fun createCommentTextArea(): JTextArea {
        return JTextArea().apply {
            text = tableComment.ifEmpty { TableStructureConstants.MSG_NO_COMMENT }
            isEditable = false
            isOpaque = true
            lineWrap = true
            wrapStyleWord = true
            font = font.deriveFont(Font.PLAIN, TableStructureConstants.DEFAULT_FONT_SIZE)
        }
    }

    private fun createSqlTextArea(): JTextArea {
        val sqlText = getSqlText()
        return JTextArea().apply {
            text = sqlText
            isEditable = false
            isOpaque = true
            font = Font(Font.MONOSPACED, Font.PLAIN, TableStructureConstants.MONOSPACED_FONT_SIZE)
            border = BorderFactory.createEmptyBorder(
                TableStructureConstants.POPUP_BORDER_SIZE,
                TableStructureConstants.POPUP_BORDER_SIZE,
                TableStructureConstants.POPUP_BORDER_SIZE,
                TableStructureConstants.POPUP_BORDER_SIZE
            )
            lineWrap = false // Do not auto-wrap, use horizontal scrolling
            wrapStyleWord = false
            componentPopupMenu = createSqlPopupMenu(this)
        }
    }

    private fun applySqlEditorColors() {
        val background = if (!JBColor.isBright()) {
            UIUtil.getPanelBackground()
        } else {
            TableStructureConstants.LIGHT_MODE_BACKGROUND
        }
        val foreground = if (!JBColor.isBright()) {
            UIUtil.getLabelForeground()
        } else {
            TableStructureConstants.LIGHT_MODE_FOREGROUND
        }

        sqlTextArea.background = background
        sqlTextArea.foreground = foreground
        sqlTextArea.caretColor = foreground
        sqlTextArea.selectedTextColor = foreground
        sqlTextArea.selectionColor = UIUtil.getTreeSelectionBackground(true)

        sqlScrollPane.background = background
        sqlScrollPane.viewport.background = background
    }

    private fun getSqlText(): String {
        return try {
            dbTable.text ?: TableStructureConstants.MSG_UNABLE_TO_GET_SQL
        } catch (e: Exception) {
            String.format(TableStructureConstants.MSG_UNABLE_TO_GET_SQL_WITH_ERROR, e.message ?: "")
        }
    }

    private fun createSqlPopupMenu(textArea: JTextArea): JPopupMenu {
        val menu = JPopupMenu().apply {
            add(JMenuItem(TableStructureConstants.MENU_COPY).apply {
                addActionListener {
                    val selectedText = textArea.selectedText ?: textArea.text
                    val clipboard = java.awt.Toolkit.getDefaultToolkit().systemClipboard
                    clipboard.setContents(java.awt.datatransfer.StringSelection(selectedText), null)
                }
            })
            add(JMenuItem(TableStructureConstants.MENU_SELECT_ALL).apply {
                addActionListener {
                    textArea.selectAll()
                }
            })
        }
        popupMenus.add(menu)
        return menu
    }

    private fun createSqlScrollPane(textArea: JTextArea): JBScrollPane {
        return JBScrollPane(textArea).apply {
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        }
    }

    /**
     * Create a table with long text display support and specified column widths
     */
    private fun <T : javax.swing.table.TableModel> createTableWithColumnWidths(
        model: T,
        columnWidths: IntArray
    ): JBTable {
        val table = createTableWithLongTextSupport(model)

        // Set column widths
        columnWidths.forEachIndexed { index, width ->
            if (index < model.columnCount) {
                table.columnModel.getColumn(index).preferredWidth = width
            }
        }

        return table
    }

    /**
     * Create a table with long text display support
     */
    private fun <T : javax.swing.table.TableModel> createTableWithLongTextSupport(model: T): JBTable {
        val table = JBTable(model)
        val renderer = MultilineTableCellRenderer()

        // Set custom renderer for all columns
        for (i in 0 until model.columnCount) {
            table.columnModel.getColumn(i).cellRenderer = renderer
        }

        // Set table header font to bold
        table.tableHeader?.let { header ->
            header.font = header.font.deriveFont(Font.BOLD)
        }

        // Add double-click listener to view full content
        val listener = TableCellPopupListener()
        table.addMouseListener(listener)
        mouseListeners.add(listener)

        // Common table settings
        table.setShowGrid(true)
        table.autoResizeMode = JTable.AUTO_RESIZE_OFF

        return table
    }

    /**
     * Create a tab with table and scroll pane
     */
    private fun createTableTab(table: JBTable): JBScrollPane {
        TableSpeedSearch.installOn(table)
        return JBScrollPane(table)
    }

    override fun getFile(): VirtualFile = file
    override fun getComponent(): JComponent = panel
    override fun getPreferredFocusedComponent(): JComponent? = panel.components.firstOrNull() as? JComponent
    override fun getName(): String = TableStructureConstants.EDITOR_NAME
    override fun setState(state: FileEditorState) {}
    override fun isModified(): Boolean = false
    override fun isValid(): Boolean = true
    override fun dispose() {
        messageBusConnection.dispose()
        // Clean up mouse listeners from all tables
        panel.components.forEach { component ->
            if (component is JScrollPane) {
                component.viewport?.view?.let { view ->
                    if (view is JTable) {
                        mouseListeners.forEach { listener ->
                            view.removeMouseListener(listener)
                        }
                    }
                }
            }
        }
        mouseListeners.clear()

        // Clean up popup menus
        popupMenus.forEach { menu ->
            menu.removeAll()
        }
        popupMenus.clear()

        // Clear panel components to help GC
        panel.removeAll()

        // Note: Models are lightweight and will be GC automatically
    }

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {}
    override fun removePropertyChangeListener(listener: PropertyChangeListener) {}
    override fun <T : Any?> getUserData(key: Key<T>): T? = null
    override fun <T : Any?> putUserData(key: Key<T>, value: T?) {}
}

