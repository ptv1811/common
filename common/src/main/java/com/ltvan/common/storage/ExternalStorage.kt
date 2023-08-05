package com.ltvan.common.storage

import android.content.Context
import com.ltvan.common.file.FileHelper
import java.io.File

/**
 * @author ltvan@fossil.com
 * on 2023-08-05
 *
 * <p>
 * </p>
 */
class ExternalStorage {
    object ExternalStorage {
        /**
         * Get a file from [Context.getExternalCacheDir].
         *
         * @param   context
         * @param   filePath relative file path from the [Context.getExternalCacheDir]
         * @return  the file.
         */
        fun getCacheFile(context: Context, filePath: String): File {
            val directory = context.externalCacheDir
            return File(directory, filePath)
        }

        /**
         * Read text from a specified cache file from [Context.getExternalCacheDir].
         *
         * @param   filePath   relative file path from [Context.getExternalCacheDir].
         *
         * @return  text content of the file, or null if the file is not existed or IO exception occurs
         * while read file.
         */
        fun readTextFromCacheFile(context: Context, filePath: String): String? {
            return FileHelper.readTextFile(getCacheFile(context, filePath))
        }

        /**
         * Save specified text to a specified cache file from [Context.getExternalCacheDir].
         *
         * @param   text    text to save
         * @param   filePath    relative file path from [Context.getExternalCacheDir].
         *
         * @return  true if the text is saved to file successfully.
         */
        fun saveTextToCacheFile(context: Context, text: String, filePath: String): Boolean {
            return FileHelper.saveTextToFile(text, getCacheFile(context, filePath))
        }

        /**
         * Read binary data from a specified cache file from [Context.getExternalCacheDir].
         *
         * @param   filePath   relative file path from [Context.getExternalCacheDir].
         *
         * @return  binary data of the file, or null if the file is not existed or IO exception occurs
         * while read file.
         */
        fun readBinaryFromCacheFile(context: Context, filePath: String): ByteArray? {
            return FileHelper.readBinaryDataFromFile(getCacheFile(context, filePath))
        }

        /**
         * Save binary data to a specified cache file from [Context.getExternalCacheDir].
         *
         * @param   data    binary data to save
         * @param   filePath    relative file path from [Context.getExternalCacheDir].
         *
         * @return  true if the binary data is saved to file successfully.
         */
        fun saveBinaryToCacheFile(context: Context, data: ByteArray, filePath: String): Boolean {
            return FileHelper.saveBinaryDataToFile(data, getCacheFile(context, filePath))
        }

        /**
         * Get a file from [Context.getExternalFilesDir(null)].
         *
         * @param   context
         * @param   filePath relative file path from the [Context.getExternalFilesDir(null)]
         * @return  the file.
         */
        fun getFile(context: Context, filePath: String): File {
            val directory = context.getExternalFilesDir(null)
            return File(directory, filePath)
        }

        /**
         * Read text from a specified file from [Context.getExternalFilesDir(null)].
         *
         * @param   filePath   relative file path from [Context.getExternalFilesDir(null)].
         *
         * @return  text content of the file, or null if the file is not existed or IO exception occurs
         * while read file.
         */
        fun readTextFromFile(context: Context, filePath: String): String? {
            return FileHelper.readTextFile(getFile(context, filePath))
        }

        /**
         * Save specified text to a specified file from [Context.getExternalFilesDir(null)].
         *
         * @param   text    text to save
         * @param   filePath    relative file path from [Context.getExternalFilesDir(null)].
         *
         * @return  true if the text is saved to file successfully.
         */
        fun saveTextToFile(context: Context, text: String, filePath: String): Boolean {
            return FileHelper.saveTextToFile(text, getFile(context, filePath))
        }

        /**
         * Read binary data from a specified file from [Context.getExternalFilesDir(null)].
         *
         * @param   filePath   relative file path from [Context.getExternalFilesDir(null)].
         *
         * @return  binary data of the file, or null if the file is not existed or IO exception occurs
         * while read file.
         */
        fun readBinaryFromFile(context: Context, filePath: String): ByteArray? {
            return FileHelper.readBinaryDataFromFile(getFile(context, filePath))
        }

        /**
         * Save binary data to a specified file from [Context.getExternalFilesDir(null)].
         *
         * @param   data    binary data to save
         * @param   filePath    relative file path from [Context.getExternalFilesDir(null)].
         *
         * @return  true if the binary data is saved to file successfully.
         */
        fun saveBinaryToFile(context: Context, data: ByteArray, filePath: String): Boolean {
            return FileHelper.saveBinaryDataToFile(data, getFile(context, filePath))
        }

        /**
         * @param   directoryPath     directory relative path directory from [Context.getExternalFilesDir(null)]
         * @param   includeSubDirectory should sub-directory included in the returned array.
         * @return                  list of files or null if the directory does not exist.
         */
        fun listFiles(
            context: Context,
            directoryPath: String,
            includeSubDirectory: Boolean = false
        ): Array<File>? {
            return listFiles(context, directoryPath, null, includeSubDirectory)
        }

        /**
         *
         * @param   directoryPath     directory relative path directory from [Context.getExternalFilesDir(null)]
         * @param   extension         empty or null to list which any file extension\
         * @return                  list of files or null if the directory does not exist.
         */
        fun listFilesWithExtension(
            context: Context,
            directoryPath: String,
            extension: String?
        ): Array<File>? {
            return listFiles(context, directoryPath, extension, false)
        }

        /**
         *
         * @param   directoryPath     directory relative path directory from [Context.getExternalFilesDir(null)]
         * @param   extension         empty or null to list which any file extension
         * @param   includeSubDirectory should sub-directory included in the returned array.
         * @return                  list of files or null if the directory does not exist.
         */
        fun listFiles(
            context: Context,
            directoryPath: String,
            extension: String?,
            includeSubDirectory: Boolean = false
        ): Array<File>? {
            val directory = getFile(context, directoryPath)
            return if (!directory.isDirectory) {
                null
            } else {
                directory.listFiles()?.filter { file ->
                    (includeSubDirectory || file.isFile) && file.name.endsWith(
                        extension ?: "",
                        true
                    )
                }?.toTypedArray()
            }
        }
    }
}