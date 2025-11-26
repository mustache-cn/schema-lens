package cn.com.mustache.plugin.schemalens.model

import com.intellij.database.psi.DbTable

/**
 * Table data container, contains all structure information of the table
 */
data class TableStructureData(
    val table: DbTable,
    val columns: List<ColumnStructure>,
    val indexes: List<IndexStructure>,
    val foreignKeys: List<ForeignKeyStructure>,
    val triggers: List<TriggerStructure>,
    val checks: List<CheckStructure>,
    val tableComment: String
)

