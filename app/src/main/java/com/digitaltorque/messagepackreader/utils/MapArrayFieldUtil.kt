package com.digitaltorque.messagepackreader.utils

import logic.Array
import logic.Field
import logic.Map

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

fun Map.keys(): List<String> {
    val list = mutableListOf<String>()
    for(i in 0 until this.keySize()) {
        list.add(this.getKey(i))
    }
    return list
}

fun Array.toList(): List<Field> {
    val list = mutableListOf<Field>()
    for(i in 0 until this.size()) {
        list.add(this.get(i))
    }
    return list
}

fun Field.value(): String {

}

fun Field.setValue(value:String) {

}