@file:Suppress("UnstableApiUsage")

package cn.com.mustache.plugin.schemalens

import cn.com.mustache.plugin.schemalens.framework.TableStructureConstants
import cn.com.mustache.plugin.schemalens.framework.TableStructureLoaderTask
import com.intellij.database.DatabaseDataKeysCore
import com.intellij.database.psi.DbElement
import com.intellij.database.psi.DbTable
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbAware

/**
 * Table structure view Action - main entry point
 */
class TableStructureAction : AnAction(), DumbAware {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT   // 或 EDT
    }

    override fun update(e: AnActionEvent) {
        val table = resolveSelectedTable(e)

        // Only show and enable menu when table is detected
        // This way the menu will only appear on table nodes
        e.presentation.isEnabledAndVisible = table != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val table = resolveSelectedTable(e) ?: return

        ProgressManager.getInstance().run(TableStructureLoaderTask(project, table))
    }

    private fun resolveSelectedTable(event: AnActionEvent): DbTable? {
        // Try different methods in order
        tryResolveByReflection(event)?.let { return it }
        tryResolveByDataKeys(event)?.let { return it }
        tryResolveBySelectedItems(event)?.let { return it }

        return null
    }

    /**
     * Helper function: find table from element (if it's a table return directly, if it's other DbElement search upward)
     */
    private fun findTable(element: Any?): DbTable? {
        when (element) {
            is DbTable -> return element
            is DbElement -> {
                // Search upward from current element to find table
                var current: DbElement? = element
                while (current != null) {
                    if (current is DbTable) return current
                    current = current.parent
                }
            }
        }
        return null
    }

    /**
     * Method 1: Try to call DatabaseContextFun.getSelectedDbElements using reflection
     */
    private fun tryResolveByReflection(event: AnActionEvent): DbTable? {
        return try {
            val databaseContextFunClass = Class.forName(TableStructureConstants.REFLECTION_CLASS_DATABASE_CONTEXT_FUN)
            val getSelectedDbElementsMethod = databaseContextFunClass.getMethod(
                TableStructureConstants.REFLECTION_METHOD_GET_SELECTED_DB_ELEMENTS,
                DataContext::class.java,
                Class::class.java
            )
            val selectedElements = getSelectedDbElementsMethod.invoke(
                null,
                event.dataContext,
                DbTable::class.java
            ) as? Iterable<*>
            selectedElements?.firstOrNull() as? DbTable
        } catch (e: Exception) {
            // Reflection failed, continue using other methods
            null
        }
    }

    /**
     * Method 2 & 3: Try to resolve using data keys
     */
    private fun tryResolveByDataKeys(event: AnActionEvent): DbTable? {
        // Method 2: Database view specific data key
        findTable(event.getData(DatabaseDataKeysCore.DB_EDITOR_OBJECT))?.let { return it }

        // Method 3: Other common data keys
        findTable(event.getData(LangDataKeys.PSI_ELEMENT))?.let { return it }
        findTable(event.getData(CommonDataKeys.PSI_ELEMENT))?.let { return it }
        findTable(event.getData(PlatformCoreDataKeys.SELECTED_ITEM))?.let { return it }

        return null
    }

    /**
     * Method 4: Get from selected items array
     */
    private fun tryResolveBySelectedItems(event: AnActionEvent): DbTable? {
        val selectedItems = event.getData(PlatformCoreDataKeys.SELECTED_ITEMS) ?: return null
        for (item in selectedItems) {
            findTable(item)?.let { return it }
        }
        return null
    }
}
