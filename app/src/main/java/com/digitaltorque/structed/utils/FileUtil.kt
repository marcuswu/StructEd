package com.digitaltorque.structed.utils

import android.content.Context
import android.net.Uri
import org.apache.commons.io.IOUtils


class FileUtil {
    companion object {
        const val MsgPackFormat = 0
        const val YamlFormat = 1
        const val JsonFormat = 2
        val stringToFormat = mapOf("MessagePack" to 0, "YAML" to 1, "JSON" to 2)
        val formatToString = mapOf(0 to "MessagePack", 1 to "YAML", 2 to "JSON")
        const val MimeMsgPack = "application/msgpack"
        const val MimeYaml = "application/yaml"
        const val MimeJson = "application/json"
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
                context.contentResolver.openOutputStream(file, "wt")?.let {
                    IOUtils.write(data, it)
                }
            } catch (e: Exception) {
                return e.message
            }
            return null
        }
    }
}
