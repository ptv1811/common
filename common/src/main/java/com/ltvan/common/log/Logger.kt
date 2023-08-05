package com.ltvan.common.log

/**
 * @author ltvan@fossil.com
 * on 2023-08-05
 *
 * <p>
 * </p>
 */
interface Logger {
    fun log(
        timeStamp: Long, level: LogLevel, tag: String, logContent: String,
        vararg logParams: Any?
    )

    fun log(level: LogLevel, tag: String, logContent: String, vararg logParams: Any?) {
        log(System.currentTimeMillis(), level, tag, logContent, logParams)
    }
}

enum class LogLevel(
    val priority: Int,
    val symbol: Char
) {
    VERBOSE(2, 'V'),
    DEBUG(3, 'D'),
    INFO(4, 'I'),
    WARN(5, 'W'),
    ERROR(6, 'E'),
    ASSERT(7, 'A');

    companion object {
        fun fromPriority(priority: Int): LogLevel? {
            return values().find {
                it.priority == priority
            }
        }
    }
}