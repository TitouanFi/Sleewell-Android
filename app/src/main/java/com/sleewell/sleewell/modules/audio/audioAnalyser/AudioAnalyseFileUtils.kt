package com.sleewell.sleewell.modules.audio.audioAnalyser

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.sleewell.sleewell.modules.audio.audioAnalyser.model.AnalyseValue
import java.io.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Manager of the audio analyse in the files
 * Can save or read analyse but also delete old analyse
 *
 * @author Hugo Berthomé
 */
class AudioAnalyseFileUtils(context: AppCompatActivity) {
    private val CLASS_TAG = "AUDIO_ANALYSE_FILE_UTIL"

    private val gson = Gson()
    private var startFile = true

    private val outputDirectory = context.cacheDir?.absolutePath + "/analyse"
    private var outputFile : File? = null
    private var outputStream: OutputStream? = null

    /**
     * Read the analyse directory and return all the files existing
     *
     * @return Array<File> array with all the file existing
     * @author Hugo Berthomé
     */
    fun readDirectory() : Array<File> {
        val dir = File(outputDirectory)

        if (!dir.exists()) {
            return Array(0) { _ -> File(".") }
        }
        val files = dir.listFiles()
        if (files != null) {
            files.sortBy { it.name }
            return files
        }
        return Array(0) { _ -> File(".") }
    }

    /**
     * Read an analyse and return the data values
     *
     * @param analyse File of the analyse to read
     * @return Array of Analyse values
     * @author Hugo Berthomé
     */
    fun readAnalyse(analyse : File) : Array<AnalyseValue> {
        val emptyArray = Array(0) {_ -> AnalyseValue()}

        if (!analyse.exists())
            return emptyArray
        try {
            val res = gson.fromJson(analyse.reader(), emptyArray.javaClass)
            if (res != null)
                return emptyArray
            return res
        } catch (eSyntax : JsonSyntaxException) {
            Log.e(CLASS_TAG, "Invalid json syntax in analyse file " + analyse.name)
            return emptyArray
        } catch (eIO : JsonIOException) {
            Log.e(CLASS_TAG, "Failed to read file  " + analyse.name)
            return emptyArray
        }
    }

    /**
     * Delete an analyse from the directory
     *
     * @param analyse File to delete
     * @author Hugo Berthomé
     */
    fun deleteAnalyse(analyse: File) {
        if (analyse.exists())
            analyse.delete()
    }

    /**
     * Delete an array of analyse from a directory
     *
     * @param analyses - Array of files
     */
    fun deleteAnalyses(analyses: Array<File>) {
        analyses.forEach { file -> deleteAnalyse(file) }
    }

    /**
     * Create the save directory in the cache
     *
     * @return true if succeed false otherwise
     */
    private fun createDir() : Boolean {
        val dir = File(outputDirectory)

        if (!dir.exists()) {
            try {
                dir.mkdirs()
            } catch (e : SecurityException) {
                Log.e(CLASS_TAG, "Directory $outputDirectory couldn't be created")
                return false
            }
        }
        return true
    }

    /**
     * Will create a new analyse file and init the header
     *
     * @return True if init with success, false otherwise
     * @author Hugo Berthomé
     */
    fun initSaveNewAnalyse() : Boolean {
        val outputFileName = getCurrentDateHour()
        outputFile = File("$outputDirectory/$outputFileName.json")
        try {
            if (!createDir())
                return false

            outputStream = FileOutputStream(outputFile)
            outputStream?.write("[".toByteArray())
            startFile = true
        } catch (e : FileNotFoundException) {
            outputFile = null
            outputStream = null
            Log.e(CLASS_TAG, "File " + outputFile?.name + " couldn't be created");
            return false
        }
        return true
    }

    /**
     * Add some data to the analyse in the file
     *
     * @param value Data value to add inside the file
     * @return True if success, false otherwise
     * @author Hugo Berthomé
     */
    fun addToAnalyse(value : AnalyseValue) : Boolean {
        if (!isSaving())
            return false

        val jsonString = gson.toJson(value)

        try {
            if (!startFile)
                outputStream?.write(",".toByteArray())
            outputStream?.write(jsonString.toByteArray())
            startFile = false
        } catch (e : IOException) {
            Log.e(CLASS_TAG, "Unable to write inside the file")
            return false
        }
        return true
    }

    /**
     * Return if an analyse file has been initialised and we can save
     *
     * @return True if can save, False otherwise
     * @author Hugo Berthomé
     */
    fun isSaving() : Boolean {
        return outputStream != null || outputFile != null
    }

    /**
     * Close the file and stop saving
     *
     * @author Hugo Berthomé
     */
    fun stopSavingNewAnalyse() {
        if (!isSaving())
            return

        outputStream?.write("]".toByteArray())
        startFile = true
        outputStream = null
        outputFile = null
    }

    /**
     * Return the current timestamp in seconds
     *
     * @return Long timestamp
     */
    private fun getCurrentTimestamp() : Long {
        return Instant.now().epochSecond
    }

    /**
     * Return the current date formatted
     *
     * @return the current date with the time
     */
    private fun getCurrentDateHour() : String {
        /*val stringTime2 = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
            .withZone(ZoneOffset.systemDefault())
            .format(Instant.ofEpochSecond(Instant.now()))*/

        return DateTimeFormatter
            .ofPattern("yyyy-MM-dd-HH:mm:ss")
            .withZone(ZoneOffset.systemDefault())
            .format(Instant.now())
    }
}