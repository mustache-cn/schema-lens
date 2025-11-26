package cn.com.mustache.plugin.schemalens.model

/**
 * Check constraint structure data class
 */
data class CheckStructure(
    var position: Int,
    val name: String,
    val expression: String,
    val comment: String
)

