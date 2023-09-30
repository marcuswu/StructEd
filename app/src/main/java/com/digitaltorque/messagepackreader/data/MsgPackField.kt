package com.digitaltorque.messagepackreader.data

import android.content.ContentResolver
import android.net.Uri
import com.daveanthonythomas.moshipack.MoshiPack
import java.io.InputStream

enum class MsgPackType {
    Nil,
    Map,
    Array,
    Int,
    Int8,
    Int16,
    Int32,
    Int64,
    Bool,
    String,
    Float32,
    Float64,
    Time,
    Unknown
}

data class MsgPackItem(val name: String?, val index: Int?, val value: Any?) {
    val items = when (value) {
        is Byte, Short, Int, Long -> null
        is Array<*> -> value.map { MsgPackItem(null, index, it)}
        is Map<*, *> -> value.map { MsgPackItem(it.key as String, null, it.value) }
        else -> null
    }

    fun toArray(): List<Any>? {
        if (items == null || !items.all { it.name == null }) {
            return null
        }
        val itemList: MutableList<Any> = mutableListOf()
        items.sortedBy { it.index }.map { itemList.add(it) }
        return itemList
    }

    fun toMap(): Map<String, Any>? {
        if (items == null || items.any { it.name == null}) {
            return null
        }
        val itemMap: MutableMap<String, Any> = mutableMapOf()
        items.map { itemMap.put(it.name ?: "", it.value ?: "") }
        return itemMap
    }
}

class MsgPack(file: Uri, contentResolver: ContentResolver) {
    var root: MsgPackItem? = null
    init {
        var istream: InputStream? = null
        try {
            istream = contentResolver.openInputStream(file)
            istream?.let {
                val packedData = istream.readBytes()
                println("Read ${packedData.size} bytes")
                val data: Map<Any, Any> = MoshiPack.unpack(packedData)
                root = MsgPackItem("root", null, data)
            }
        } catch (e: Exception) {
            root = null
        } finally {
            istream?.close()
        }
    }
}