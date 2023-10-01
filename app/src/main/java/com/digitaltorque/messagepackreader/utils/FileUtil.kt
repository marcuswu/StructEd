package com.digitaltorque.messagepackreader.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import org.apache.commons.io.IOUtils
import java.io.File


class FileUtil {
    companion object {
        fun readFile(context: Context, file: Uri): ByteArray? {
            try {
                return context.contentResolver.openInputStream(file)?.let {
                    return@let try {
                        IOUtils.toByteArray(it)
                    } catch (e: Exception) {
                        null
                    }
                }
            } catch(e: Exception) {
                return null
            }
        }

        fun writeFile(context: Context, data: ByteArray, file: Uri): String? {
            try {
                context.contentResolver.openOutputStream(file)?.let {
                    IOUtils.write(data, it)
                }
            } catch (e: Exception) {
                return e.message
            }
            return null
        }

        fun filePath(context: Context, file: Uri): String? {
            return when(true) {
                isExternalStorageDocument(file) -> externalStorageDocumentPath(file)
                isDownloadsDocument(file) -> downloadsDocumentPath(context, file)
                isMediaDocument(file) -> mediaDocumentPath(context, file)
                else -> null
            }
        }

        private fun downloadsDocumentPath(context: Context, uri: Uri): String? {
            val fileName = getFilePath(context, uri)
            fileName?.let { return "${Environment.getExternalStorageDirectory()} /Download/$fileName" }
            var id = DocumentsContract.getDocumentId(uri)
            if (id.startsWith("raw:")) {
                id = id.replaceFirst("raw:".toRegex(), "")
                val file = File(id)
                if (file.exists()) return id
            }
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"),
                java.lang.Long.valueOf(id)
            )
            return getDataColumn(context, contentUri, null, null)
        }

        private fun mediaDocumentPath(context: Context, uri: Uri): String? {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            when (type) {
                "image" -> {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                "video" -> {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }
                "audio" -> {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(
                split[1]
            )
            return contentUri?.let { getDataColumn(context, it, selection, selectionArgs) }
        }

        private fun externalStorageDocumentPath(uri: Uri): String? {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()

            val fullPath = getPathFromExtSD(split)
            fullPath?.takeIf { fullPath.isNotEmpty() }?.let { return fullPath }
            return null
        }

        private fun fileExists(filePath: String): Boolean {
            val file = File(filePath)
            return file.exists()
        }

        private fun getPathFromExtSD(pathData: Array<String>): String? {
            val type = pathData[0]
            val relativePath = "/" + pathData[1]
            var fullPath: String?

            // on my Sony devices (4.4.4 & 5.1.1), `type` is a dynamic string
            // something like "71F8-2C0A", some kind of unique id per storage
            // don't know any API that can get the root path of that storage based on its id.
            //
            // so no "primary" type, but let the check here for other devices
            if ("primary".equals(type, ignoreCase = true)) {
                fullPath = Environment.getExternalStorageDirectory().toString() + relativePath
                if (fileExists(fullPath)) {
                    return fullPath
                }
            }

            // Environment.isExternalStorageRemovable() is `true` for external and internal storage
            // so we cannot relay on it.
            //
            // instead, for each possible path, check if file exists
            // we'll start with secondary storage as this could be our (physically) removable sd card
            fullPath = System.getenv("SECONDARY_STORAGE")?.plus(relativePath)
            fullPath?.takeIf { fileExists(it) }?.let {
                return it
            }
            fullPath = System.getenv("EXTERNAL_STORAGE")?.plus(relativePath)
            fullPath?.takeIf { fileExists(it) }?.let {
                return it
            }
            return fullPath
        }

        private fun getDataColumn(
            context: Context,
            uri: Uri,
            selection: String?,
            selectionArgs: Array<String>?
        ): String? {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)
            try {
                cursor = context.contentResolver.query(
                    uri, projection,
                    selection, selectionArgs, null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(index)
                }
            } finally {
                cursor?.close()
            }
            return null
        }

        private fun getFilePath(context: Context, uri: Uri?): String? {
            var cursor: Cursor? = null
            val projection = arrayOf(
                MediaStore.MediaColumns.DISPLAY_NAME
            )
            try {
                cursor = context.contentResolver.query(
                    uri!!, projection, null, null,
                    null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                    return cursor.getString(index)
                }
            } finally {
                cursor?.close()
            }
            return null
        }

        private fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        private fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        private fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        private fun isGooglePhotosUri(uri: Uri): Boolean {
            return "com.google.android.apps.photos.content" == uri.authority
        }

        fun isWhatsAppFile(uri: Uri): Boolean {
            return "com.whatsapp.provider.media" == uri.authority
        }

        private fun isGoogleDriveUri(uri: Uri): Boolean {
            return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
        }
    }
}
