package de.hsworms.videokurse.model

import android.content.Context
import java.io.InputStream

class FileManager private constructor(val appContext: Context) {

    companion object {
        private var INSTANCE: FileManager? = null

        fun initialize(context: Context) : FileManager {
            if (INSTANCE == null) {
                INSTANCE = FileManager(context)
            }
            return INSTANCE as FileManager
        }

        fun get(): FileManager {
            return INSTANCE ?:
            throw IllegalStateException("FileManager not initialized.")
        }
    }

    fun readFileFromAppRessources(resId: Int) : InputStream {
        val inputStream = appContext.getResources().openRawResource(resId)
        return inputStream
    }

}

