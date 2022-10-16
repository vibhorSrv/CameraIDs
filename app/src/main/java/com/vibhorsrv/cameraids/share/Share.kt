package com.vibhorsrv.cameraids.share

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.net.URLConnection

object Share {
    fun shareUri(context: Context, uri: Uri, extraText: String?) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, extraText)
            type = URLConnection.guessContentTypeFromName(uri.toString())
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, null))
    }
}
