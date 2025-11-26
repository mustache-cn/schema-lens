package cn.com.mustache.plugin.schemalens.extractor

import cn.com.mustache.plugin.schemalens.framework.TableStructureLogger
import cn.com.mustache.plugin.schemalens.model.CheckStructure
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbObject
import com.intellij.database.psi.DbTable

/**
 * Check constraint extractor
 */
object CheckExtractor {
    fun extract(table: DbTable): List<CheckStructure> {
        return try {
            // Use table.dasObject.getDasChildren(ObjectKind.CHECK) to get check constraints
            val dasObject = table.dasObject
            val checks = dasObject.getDasChildren(ObjectKind.CHECK)
            checks.mapIndexedNotNull { index, check ->
                check?.toCheckStructure()?.apply { this.position = index + 1 }
            }.toList()
        } catch (e: Throwable) {
            TableStructureLogger.warn("Failed to extract check constraints from ${table.name}", e)
            emptyList()
        }
    }

    // Convert check constraint object to CheckStructure
    private fun Any.toCheckStructure(): CheckStructure? {
        return try {
            val check = this as DbObject

            CheckStructure(
                name = check.name,
                expression = check.text,
                comment = check.comment ?: "",
                position = 0
            )
        } catch (_: Exception) {
            null
        }
    }
}

