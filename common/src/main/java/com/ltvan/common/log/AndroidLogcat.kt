package com.ltvan.common.log

import android.util.Log

/**
 * @author ltvan@fossil.com
 * on 2023-08-05
 *
 * <p>
 * </p>
 */
class AndroidLogcat : Logger {
    override fun log(
        timeStamp: Long,
        level: LogLevel,
        tag: String,
        logContent: String,
        vararg logParams: Any?
    ) {
        Log.println(level.priority, tag, String.format(logContent, *logParams))
    }
}