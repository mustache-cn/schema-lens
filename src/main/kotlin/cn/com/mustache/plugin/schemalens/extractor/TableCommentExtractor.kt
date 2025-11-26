package cn.com.mustache.plugin.schemalens.extractor

import com.intellij.database.psi.DbTable

/**
 * Table comment extractor - extracts table comment information
 */
object TableCommentExtractor {
    fun extract(table: DbTable): String {
        return table.comment ?: ""
    }
}

