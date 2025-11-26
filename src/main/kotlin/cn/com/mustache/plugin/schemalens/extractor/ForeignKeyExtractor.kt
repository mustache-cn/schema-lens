package cn.com.mustache.plugin.schemalens.extractor

import cn.com.mustache.plugin.schemalens.framework.TableStructureConstants
import cn.com.mustache.plugin.schemalens.framework.TableStructureLogger
import cn.com.mustache.plugin.schemalens.model.ForeignKeyStructure
import com.intellij.database.model.DasForeignKey
import com.intellij.database.psi.DbTable
import com.intellij.database.util.DasUtil

/**
 * Foreign key extractor - extracts foreign key information from database tables.
 *
 * This extractor uses [DasUtil.getForeignKeys] to retrieve foreign key information
 * and converts it to [ForeignKeyStructure] objects for display in the UI.
 *
 * @see DasUtil.getForeignKeys
 * @see ForeignKeyStructure
 */
object ForeignKeyExtractor {
    fun extract(table: DbTable): List<ForeignKeyStructure> {
        return try {
            // Use DasUtil.getForeignKeys to directly get foreign keys
            // Note: This call should be executed in ReadAction (already handled in TableStructureLoaderTask)
            val dasForeignKeys = DasUtil.getForeignKeys(table)

            // Convert DbForeignKey to ForeignKeyStructure
            dasForeignKeys.mapIndexed { index, it -> it.toForeignKeyStructure().apply { this.position = index + 1 } }
                .toList()
        } catch (e: Throwable) {
            // Catch all exceptions, including DataGrip platform internal warnings
            // These warnings do not affect functionality but will be shown in logs
            TableStructureLogger.warn("Failed to extract foreign keys from ${table.name}", e)
            emptyList()
        }
    }

    // Convert DbForeignKey to ForeignKeyStructure
    private fun DasForeignKey.toForeignKeyStructure(): ForeignKeyStructure {
        // Get foreign key columns
        val columns = columnsRef.names().joinToString(TableStructureConstants.COLUMN_SEPARATOR)

        // Get referenced table
        val referencedTable = refTableName ?: ""

        // Get referenced columns
        val referencedColumns =
            refColumns.names().joinToString(TableStructureConstants.COLUMN_SEPARATOR)

        // Get update rule
        val onUpdate = updateRule.name

        // Get delete rule
        val onDelete = deleteRule.name

        // Get comment
        val comment = comment ?: ""

        // Optimized: use StringBuilder for string concatenation
        val finalName = name.ifEmpty {
            StringBuilder(TableStructureConstants.FK_PREFIX.length + (table?.name?.length ?: 0))
                .append(TableStructureConstants.FK_PREFIX)
                .append(table?.name)
                .toString()
        }

        return ForeignKeyStructure(
            name = finalName,
            columns = columns,
            referencedTable = referencedTable,
            referencedColumns = referencedColumns,
            onUpdate = onUpdate,
            onDelete = onDelete,
            comment = comment,
            position = 0
        )
    }

}

