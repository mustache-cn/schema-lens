package cn.com.mustache.plugin.schemalens.model

/**
 * Index structure data class
 */
data class IndexStructure(
    var position: Int,
    val name: String,
    val columns: String,
    val unique: Boolean,
    val type: String,
    val comment: String
)

