package cn.com.mustache.plugin.schemalens.model

/**
 * Column structure data class
 */
data class ColumnStructure(
    var position: Int,
    val name: String,
    val dataType: String,
    val nullable: Boolean,
    val defaultValue: String?,
    val primaryKey: Boolean,
    val autoIncrement: Boolean,
    val comment: String
)

