package cn.com.mustache.plugin.schemalens.extractor

import cn.com.mustache.plugin.schemalens.framework.TableStructureConstants
import cn.com.mustache.plugin.schemalens.framework.TableStructureLogger
import cn.com.mustache.plugin.schemalens.model.TriggerStructure
import com.intellij.database.model.DasTrigger
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbTable
import com.intellij.database.util.common.asOptional

/**
 * Trigger extractor
 */
object TriggerExtractor {
    fun extract(table: DbTable): List<TriggerStructure> {
        return try {
            // Use table.dasObject.getDasChildren(ObjectKind.TRIGGER) to get triggers
            val dasObject = table.dasObject
            val triggers = dasObject.getDasChildren(ObjectKind.TRIGGER)
            triggers.mapIndexedNotNull { index, trigger ->
                (trigger as? DasTrigger)?.toTriggerStructure().apply { this?.position = index + 1 }
            }.toList()
        } catch (e: Throwable) {
            TableStructureLogger.warn("Failed to extract triggers from ${table.name}", e)
            emptyList()
        }
    }

    // Convert DasTrigger to TriggerStructure
    private fun DasTrigger.toTriggerStructure(): TriggerStructure {

        // Get event type (INSERT, UPDATE, DELETE)
        // Note: DasTrigger may not have direct event property, need to get through other means
        val event = events.joinToString(TableStructureConstants.COLUMN_SEPARATOR)

        return TriggerStructure(
            name = name,
            event = event,
            timing = turn?.name.toString(),
            statement = asOptional.get().toString(),
            comment = comment ?: "",
            position = 0
        )
    }
}

