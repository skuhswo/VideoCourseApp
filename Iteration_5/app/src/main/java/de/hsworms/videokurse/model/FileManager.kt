package de.hsworms.videokurse.model

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileManager @Inject constructor(
    @ApplicationContext val context: Context
) {

    fun readFileFromAppResources(resId: Int): InputStream {
        return context.resources.openRawResource(resId)
    }

}

