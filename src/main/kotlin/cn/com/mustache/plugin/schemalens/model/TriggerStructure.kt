package cn.com.mustache.plugin.schemalens.model

/**
 * Trigger structure data class
 */
data class TriggerStructure(
    var position: Int,
    val name: String,
    val event: String,
    val timing: String,
    val statement: String,
    val comment: String
)

