package cn.com.mustache.plugin.schemalens.extractor

import cn.com.mustache.plugin.schemalens.framework.TableStructureConstants
import cn.com.mustache.plugin.schemalens.framework.TableStructureLogger
import cn.com.mustache.plugin.schemalens.model.IndexStructure
import com.intellij.database.model.DasIndex
import com.intellij.database.psi.DbTable
import com.intellij.database.util.DasUtil

/**
 * Index extractor - extracts index information from database tables.
 *
 * This extractor uses [DasUtil.getIndices] to retrieve index information
 * and converts it to [IndexStructure] objects for display in the UI.
 *
 * @see DasUtil.getIndices
 * @see IndexStructure
 */
object IndexExtractor {
    fun extract(table: DbTable): List<IndexStructure> {
        return try {
            // Use DasUtil.getIndices to directly get indices
            // Note: This call should be executed in ReadAction (already handled in TableStructureLoaderTask)
            val dasIndexes = DasUtil.getIndices(table)

            // Convert DasIndex to IndexStructure
            dasIndexes.mapIndexed { index, it -> it.toIndexStructure().apply { this.position = index + 1 } }.toList()
        } catch (e: Throwable) {
            // Catch all exceptions, including DataGrip platform internal warnings
            // These warnings do not affect functionality but will be shown in logs
            TableStructureLogger.warn("Failed to extract indexes from ${table.name}", e)
            emptyList()
        }
    }

    // Convert DasIndex to IndexStructure
    private fun DasIndex.toIndexStructure(): IndexStructure {
        // Get index column information
        val columnNames = columnsRef.names().toList().joinToString(TableStructureConstants.COLUMN_SEPARATOR)

        // Determine index type (cache uppercase name to avoid multiple calls)
        val nameUpper = name.uppercase()
        val isPrimary = isPrimaryIndex(nameUpper)

        val isUnique = isUnique

        val indexType = determineIndexType(isPrimary, isUnique)

        return IndexStructure(
            name = name,
            columns = columnNames.ifEmpty { name },
            unique = isPrimary || isUnique,
            type = indexType,
            comment = comment.orEmpty(),
            position = 0
        )
    }

    /**
     * Check if index is primary key
     */
    private fun isPrimaryIndex(nameUpper: String): Boolean {
        return nameUpper.contains(TableStructureConstants.INDEX_TYPE_PRIMARY) ||
                nameUpper.startsWith(TableStructureConstants.INDEX_PREFIX_PRIMARY) ||
                nameUpper == TableStructureConstants.INDEX_NAME_PRIMARY
    }

    /**
     * Determine index type based on flags
     */
    private fun determineIndexType(isPrimary: Boolean, isUnique: Boolean): String {
        return when {
            isPrimary -> TableStructureConstants.INDEX_TYPE_PRIMARY
            isUnique -> TableStructureConstants.INDEX_TYPE_UNIQUE
            else -> TableStructureConstants.INDEX_TYPE_INDEX
        }
    }
}

