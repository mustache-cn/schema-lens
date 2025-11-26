package cn.com.mustache.plugin.schemalens.framework

import cn.com.mustache.plugin.schemalens.ui.TableStructureFileEditor
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import java.awt.BorderLayout
import java.beans.PropertyChangeListener
import javax.swing.JPanel

/**
 * Table structure editor provider
 */
class TableStructureEditorProvider : FileEditorProvider, DumbAware {
    override fun accept(project: Project, file: VirtualFile): Boolean {
        return file is TableStructureVirtualFile
    }

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        val virtualFile = file as? TableStructureVirtualFile ?: return createEmptyEditor(file)
        val data = virtualFile.getUserData(TableStructureConstants.TABLE_DATA_KEY) ?: return createEmptyEditor(file)
        val table = data.table
        val columns = data.columns
        val indexes = data.indexes
        val foreignKeys = data.foreignKeys
        val triggers = data.triggers
        val checks = data.checks
        val tableComment = data.tableComment

        return TableStructureFileEditor(
            virtualFile,
            table,
            columns,
            indexes,
            foreignKeys,
            triggers,
            checks,
            tableComment
        )
    }

    private fun createEmptyEditor(file: VirtualFile): FileEditor {
        return object : FileEditor {
            private val panel = JPanel(BorderLayout())
            override fun getFile(): VirtualFile = file
            override fun getComponent() = panel
            override fun getPreferredFocusedComponent() = null
            override fun getName() = "Structure"
            override fun setState(state: FileEditorState) {}
            override fun isModified() = false
            override fun isValid() = true
            override fun dispose() {}
            override fun addPropertyChangeListener(listener: PropertyChangeListener) {}
            override fun removePropertyChangeListener(listener: PropertyChangeListener) {}
            override fun <T : Any?> getUserData(key: Key<T>): T? = null
            override fun <T : Any?> putUserData(key: Key<T>, value: T?) {}
        }
    }

    override fun getEditorTypeId(): String = "TableStructureEditor"
    override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.HIDE_DEFAULT_EDITOR
}

