package cn.com.mustache.plugin.schemalens.framework

import com.intellij.testFramework.LightVirtualFile

/**
 * Virtual file for displaying table structure in editor
 */
class TableStructureVirtualFile(
    tableName: String
) : LightVirtualFile(tableName, TableStructureFileType, "") {
    init {
        isWritable = false
    }
}

