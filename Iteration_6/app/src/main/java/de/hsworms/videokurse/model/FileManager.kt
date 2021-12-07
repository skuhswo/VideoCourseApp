package de.hsworms.videokurse.model

import android.content.Context
import android.util.Log
import com.beust.klaxon.JsonObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class FileManager @Inject constructor(
    @ApplicationContext val context: Context
) {

    fun readFileFromAppResources(resId: Int): InputStream {
        return context.resources.openRawResource(resId)
    }

    fun readLocalFile(filePath: String, fileName: String): InputStream {
        val dir = File(context.filesDir, filePath)
        val file = File(dir, fileName)
        return file.inputStream()
    }

    private fun makeAndGetLocalDirectory(filePath: String) : File {
        val dir = File(context.filesDir, filePath)
        dir.mkdir()
        return dir
    }

    suspend fun downloadFileFromFirebase(filePath: String, fileName: String) {
        try {
            val storage = Firebase.storage
            val storageRef = storage.reference
            val catalogRef = storageRef.child(filePath + "/" + fileName)

            val dir: File =
                if (filePath != "")
                    makeAndGetLocalDirectory(filePath)
                else
                    context.filesDir
            val localFile = File(dir, fileName)

            catalogRef.getFile(localFile).await()
        } catch (e: Exception) {
            // add error handling
            Log.d("File Manager", e.message?: "")
        }
    }

}

