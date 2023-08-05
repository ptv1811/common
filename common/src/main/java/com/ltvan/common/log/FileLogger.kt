package com.ltvan.common.log

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import com.ltvan.common.file.FileHelper
import com.ltvan.common.storage.InternalStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author ltvan@fossil.com
 * on 2023-08-05
 *
 * <p>
 * </p>
 */
class FileLogger
constructor(
    private val context: Context,
    private var logDirectory: String,
    private var maximumLogFileSize: Int,
    private var maximumNumberOfLogFile: Int,
    private var dateTimeFormat: SimpleDateFormat,
    private val preferenceFileName: String
) : Logger,
    HandlerThread(
        FileLogger::class.java.name,
        android.os.Process.THREAD_PRIORITY_BACKGROUND
    ) {
    private val handler: Handler

    init {
        start()
        handler = Handler(looper)
    }

    companion object {
        private val NEW_LINE = System.getProperty("line.separator")
        private const val FILE_COUNT = "file_count"

        @Suppress("SpellCheckingInspection")
        private const val LOG_FILE_NAME_TEMPLATE = "sdk_log_%010d.sdklog"
        private const val LOG_FILE_START_INDEX = 0
        private const val LOG_FIELD_SEPARATOR = " "
    }

    override fun log(
        timeStamp: Long, level: LogLevel, tag: String, logContent: String,
        vararg logParams: Any?
    ) {
        val logMessage = decorateLogMessage(
            level, tag, String.format(logContent, *logParams),
            timeStamp
        )
        handler.post {
            getLogFile()?.run {
                FileHelper.appendTextFile(logMessage, this)
            }
        }
    }

    private fun decorateLogMessage(
        level: LogLevel,
        tag: String,
        logContent: String,
        timeStamp: Long = System.currentTimeMillis()
    ): String {
        val date = Date(timeStamp)
        val builder = StringBuilder()
        // machine-readable date/time
        builder.append(date.time.toString())
        // human-readable date/time
        builder.append(LOG_FIELD_SEPARATOR)
        builder.append(dateTimeFormat.format(date))
        // level
        builder.append(LOG_FIELD_SEPARATOR)
        builder.append(level.symbol)
        // tag
        builder.append(LOG_FIELD_SEPARATOR)
        builder.append(tag)
        // message
        builder.append(LOG_FIELD_SEPARATOR)
        builder.append(logContent)
        // new line
        builder.append(NEW_LINE)
        return builder.toString()
    }

    private fun getLogFile(): File? {
        val sharedPreferences = context.getSharedPreferences(
            preferenceFileName,
            Context.MODE_PRIVATE
        )
        val currentFileIndex = sharedPreferences?.getInt(FILE_COUNT, LOG_FILE_START_INDEX)
            ?: LOG_FILE_START_INDEX
        val fileDir = InternalStorage.getFile(context, logDirectory)
        // TODO: Handle if fileDir is null
        fileDir?.run {
            if (!fileDir.exists()) {
                // TODO: Handle if create directory fail
                fileDir.mkdirs()
            }
            val file = InternalStorage.getFile(
                context,
                getLogFilePath(currentFileIndex)
            )
            // TODO: Handle if file is null
            file?.run {
                return if (file.length() >= maximumLogFileSize) {
                    val nextFileIndex = getNextFileIndex(currentFileIndex)
                    sharedPreferences?.edit()?.putInt(FILE_COUNT, nextFileIndex)?.apply()
                    val fileIndexToDelete = getPreviousFileIndexToDelete(nextFileIndex)
                    // TODO: Handle if delete file fail
                    InternalStorage.getFile(
                        context,
                        getLogFilePath(fileIndexToDelete)
                    )?.delete()
                    getLogFile()
                } else {
                    file
                }
            }
        }
        return null
    }

    private fun getLogFilePath(fileIndex: Int): String {
        return logDirectory + File.separatorChar + buildLogFileName(fileIndex)
    }

    private fun buildLogFileName(fileIndex: Int): String {
        return String.format(LOG_FILE_NAME_TEMPLATE, fileIndex)
    }

    private fun getNextFileIndex(currentFileIndex: Int): Int {
        return ((currentFileIndex.toLong() + 1 + Int.MAX_VALUE) % Int.MAX_VALUE).toInt()
    }

    private fun getPreviousFileIndexToDelete(currentFileIndex: Int): Int {
        return ((currentFileIndex.toLong() - maximumNumberOfLogFile +
                Int.MAX_VALUE) % Int.MAX_VALUE).toInt()
    }

}