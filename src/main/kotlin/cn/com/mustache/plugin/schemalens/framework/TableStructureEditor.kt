package cn.com.mustache.plugin.schemalens.framework

import cn.com.mustache.plugin.schemalens.model.*
import cn.com.mustache.plugin.schemalens.model.TableStructureData
import com.intellij.database.psi.DbTable
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project

/**
 * Table structure editor utility class - responsible for opening the editor
 */
object TableStructureEditor {
    fun openInEditor(
        project: Project,
        table: DbTable,
        columns: List<ColumnStructure>,
        indexes: List<IndexStructure>,
        foreignKeys: List<ForeignKeyStructure>,
        triggers: List<TriggerStructure>,
        checks: List<CheckStructure>,
        tableComment: String
    ) {
        val virtualFile = TableStructureVirtualFile(table.name)
        virtualFile.putUserData(
            TableStructureConstants.TABLE_DATA_KEY,
            TableStructureData(table, columns, indexes, foreignKeys, triggers, checks, tableComment)
        )

        val editorManager = FileEditorManager.getInstance(project)
        editorManager.openFile(virtualFile, true)
    }
}

