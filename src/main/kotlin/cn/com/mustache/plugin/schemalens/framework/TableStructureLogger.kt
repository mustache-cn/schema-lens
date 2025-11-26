package cn.com.mustache.plugin.schemalens.framework

import com.intellij.openapi.diagnostic.Logger

/**
 * Logger for table structure plugin
 */
object TableStructureLogger {
    private val logger: Logger = Logger.getInstance("cn.com.mustache.plugin.schemalens")

    fun debug(message: String) {
        logger.debug(message)
    }

    fun info(message: String) {
        logger.info(message)
    }

    fun warn(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            logger.warn(message, throwable)
        } else {
            logger.warn(message)
        }
    }

    fun error(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            logger.error(message, throwable)
        } else {
            logger.error(message)
        }
    }
}

