package cn.com.mustache.plugin.schemalens.model

/**
 * Foreign key structure data class
 */
data class ForeignKeyStructure(
    var position: Int,
    val name: String,
    val columns: String,
    val referencedTable: String,
    val referencedColumns: String,
    val onUpdate: String,
    val onDelete: String,
    val comment: String
)

