package cn.com.mustache.plugin.schemalens.framework

import cn.com.mustache.plugin.schemalens.extractor.*
import cn.com.mustache.plugin.schemalens.model.*
import com.intellij.database.psi.DbTable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

/**
 * Data class to hold all extracted table structure data
 */
private data class TableStructureData(
    val columns: List<ColumnStructure>,
    val indexes: List<IndexStructure>,
    val foreignKeys: List<ForeignKeyStructure>,
    val triggers: List<TriggerStructure>,
    val checks: List<CheckStructure>,
    val tableComment: String
)

/**
 * Table structure loading task - extracts table structure information in background thread
 */
class TableStructureLoaderTask(
    project: Project,
    private val table: DbTable
) : Task.Backgroundable(project, "Loading ${table.name} structure", true) {

    private val projectRef: Project = project

    override fun run(indicator: ProgressIndicator) {
        try {
            val tableName = table.name // Cache table name to avoid repeated property access

            // Extract all table structure data
            val structureData = extractTableStructureData(indicator, tableName)

            // Open editor with extracted data
            openEditor(structureData)
        } catch (e: Exception) {
            handleError(e)
        }
    }

    /**
     * Extract all table structure data
     */
    private fun extractTableStructureData(
        indicator: ProgressIndicator,
        tableName: String
    ): TableStructureData {
        indicator.text = "Extracting metadata from $tableName"
        val columns = safeExtract(
            extractor = { TableStructureExtractor.extract(table) },
            defaultValue = emptyList(),
            errorMessage = "Failed to extract columns from $tableName"
        )

        indicator.text = "Extracting indexes from $tableName"
        val indexes = safeExtract(
            extractor = { IndexExtractor.extract(table) },
            defaultValue = emptyList(),
            errorMessage = "Failed to extract indexes from $tableName",
            isThrowable = true
        )

        indicator.text = "Extracting foreign keys from $tableName"
        val foreignKeys = safeExtract(
            extractor = { ForeignKeyExtractor.extract(table) },
            defaultValue = emptyList(),
            errorMessage = "Failed to extract foreign keys from $tableName",
            isThrowable = true
        )

        indicator.text = "Extracting triggers from $tableName"
        val triggers = safeExtract(
            extractor = { TriggerExtractor.extract(table) },
            defaultValue = emptyList(),
            errorMessage = "Failed to extract triggers from $tableName",
            isThrowable = true
        )

        indicator.text = "Extracting check constraints from $tableName"
        val checks = safeExtract(
            extractor = { CheckExtractor.extract(table) },
            defaultValue = emptyList(),
            errorMessage = "Failed to extract check constraints from $tableName",
            isThrowable = true
        )

        indicator.text = "Extracting table comment from $tableName"
        val tableComment = safeExtract(
            extractor = { TableCommentExtractor.extract(table) },
            defaultValue = "",
            errorMessage = "Failed to extract table comment from $tableName"
        )

        return TableStructureData(columns, indexes, foreignKeys, triggers, checks, tableComment)
    }

    /**
     * Open editor with extracted data
     */
    private fun openEditor(data: TableStructureData) {
        ApplicationManager.getApplication().invokeLater {
            try {
                TableStructureEditor.openInEditor(
                    projectRef,
                    table,
                    data.columns,
                    data.indexes,
                    data.foreignKeys,
                    data.triggers,
                    data.checks,
                    data.tableComment
                )
            } catch (e: Exception) {
                TableStructureLogger.error("Failed to open table structure editor for ${table.name}", e)
                Messages.showErrorDialog(
                    projectRef,
                    "Failed to open table structure editor: ${e.message}",
                    "Error"
                )
            }
        }
    }

    /**
     * Handle errors during extraction
     */
    private fun handleError(e: Exception) {
        TableStructureLogger.error("Failed to load table structure for ${table.name}", e)
        ApplicationManager.getApplication().invokeLater {
            Messages.showErrorDialog(
                projectRef,
                "Failed to load table structure: ${e.message}",
                "Error"
            )
        }
    }

    /**
     * Safely execute extraction with error handling and logging
     */
    private fun <T> safeExtract(
        extractor: () -> T,
        defaultValue: T,
        errorMessage: String,
        isThrowable: Boolean = false
    ): T {
        return ReadAction.compute<T, RuntimeException> {
            try {
                extractor()
            } catch (e: Throwable) {
                if (isThrowable) {
                    // Note: DataGrip platform may generate some internal warnings, these do not affect functionality
                    TableStructureLogger.warn("$errorMessage (platform warning, may be safe to ignore)", e)
                } else {
                    TableStructureLogger.warn(errorMessage, e)
                }
                defaultValue
            }
        }
    }
}

