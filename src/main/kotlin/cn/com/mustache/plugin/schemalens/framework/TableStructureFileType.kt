package cn.com.mustache.plugin.schemalens.framework

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

/**
 * Virtual file type for SchemaLens table structure view.
 * Only used to provide a custom icon.
 */
object TableStructureFileType : FileType {

    override fun getName(): String = "SchemaLensTableStructure"

    override fun getDescription(): String = "SchemaLens table structure virtual file"

    override fun getDefaultExtension(): String = ""   // 没有实际扩展名

    override fun getIcon(): Icon =
        IconLoader.getIcon("/icons/icon.svg", TableStructureFileType::class.java)

    override fun isBinary(): Boolean = false

    override fun isReadOnly(): Boolean = true

    override fun getCharset(file: VirtualFile, content: ByteArray): String = "UTF-8"
}