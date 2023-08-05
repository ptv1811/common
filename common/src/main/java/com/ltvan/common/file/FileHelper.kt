package com.ltvan.common.file

import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * @author ltvan@fossil.com
 * on 2023-08-05
 *
 * <p>
 * </p>
 */
object FileHelper {
    fun readTextFile(file: File): String? {
        if (!file.exists())
            return null
        var reader: InputStreamReader? = null
        return try {
            val stringBuilder = StringBuilder()
            reader = InputStreamReader(FileInputStream(file))
            val chars = CharArray(1024)
            var length = reader.read(chars)
            while (length != -1) {
                stringBuilder.append(chars, 0, length)
                length = reader.read(chars)
            }
            stringBuilder.toString()
        } catch (e: IOException) {
            null
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    // Fail silently
                }
            }
        }
    }

    /**
     * Save specified text to specified file.
     *
     * @param   text    text to write.
     * @param   file    file to write.
     *
     * @return  result of saving text to file.
     */
    fun saveTextToFile(text: String, file: File): Boolean {
        var result = true
        var writer: OutputStreamWriter? = null
        try {
            if (!file.exists()) {
                file.createNewFile()
            }
            writer = OutputStreamWriter(FileOutputStream(file))
            writer.write(text)
        } catch (e: IOException) {
            result = false
        } finally {
            if (writer != null) {
                try {
                    writer.close()
                } catch (e: IOException) {
                    result = false
                }
            }
        }
        return result
    }

    /**
     * Read data from a specified file.
     *
     * @param   file   file to read text.
     *
     * @return  data of the file, or null if the file is not existed.
     *
     */
    @Suppress("unused")
    fun readBinaryDataFromFile(file: File): ByteArray? {
        val dataInputStream: DataInputStream
        try {
            dataInputStream = DataInputStream(FileInputStream(file))
        } catch (e: FileNotFoundException) {
            return null
        }
        val fileData = ByteArray(file.length().toInt())
        try {
            dataInputStream.readFully(fileData)
        } catch (e: IOException) {
            return null
        } finally {
            try {
                dataInputStream.close()
            } catch (e: IOException) {
                // Fail silently
            }
        }
        return fileData
    }

    /**
     * Save specified data to specified file.
     *
     * @param   data    data to write.
     * @param   file    file to write.
     *
     * @return  result of saving data to file.
     */
    @Suppress("unused")
    fun saveBinaryDataToFile(data: ByteArray, file: File): Boolean {
        var result = true
        var outStream: FileOutputStream? = null
        try {
            if (!file.exists()) {
                file.createNewFile()
            }
            outStream = FileOutputStream(file)
            outStream.write(data)
        } catch (e: IOException) {
            result = false
        } finally {
            if (outStream != null) {
                try {
                    outStream.close()
                } catch (e: IOException) {
                    result = false
                }
            }
        }
        return result
    }

    /**
     * Append text to a text file.
     *
     * @param   text    text to save
     * @param   file    file to append
     *
     * @return  the result of appending text to file.
     */
    fun appendTextFile(text: String, file: File): Boolean {
        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(file, true)
            fileWriter.append(text)
            fileWriter.flush()
            fileWriter.close()
            return true
        } catch (e: IOException) {
            try {
                fileWriter?.flush()
                fileWriter?.close()
            } catch (e: IOException) {
                // Fail silently
            }
            return false
        }
    }
}