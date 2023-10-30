package com.digitaltorque.structed.utils

import logic.Field
import logic.Map
import java.time.Instant

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

fun Map.keys(path: String): List<String> {
    val list = mutableListOf<String>()
    for(i in 0 until this.keySizeAt(path)) {
        list.add(this.getKeyAt(path, i))
    }
    return list
}

fun Field.valueOrType(): String {
    return when (this.type().toInt()) {
        MsgPackType.String.ordinal -> this.string
        MsgPackType.Int.ordinal -> this.int.toString()
        MsgPackType.Int8.ordinal -> this.int8.toString()
        MsgPackType.Int16.ordinal -> this.int16.toString()
        MsgPackType.Int32.ordinal -> this.int32.toString()
        MsgPackType.Int64.ordinal -> this.int64.toString()
        MsgPackType.Float32.ordinal -> this.float32.toString()
        MsgPackType.Float64.ordinal -> this.float64.toString()
        MsgPackType.Bool.ordinal -> this.bool.toString()
        MsgPackType.Nil.ordinal -> "null"
        MsgPackType.Time.ordinal -> Instant.ofEpochMilli(this.time).toString()
        MsgPackType.Array.ordinal -> "List"
        MsgPackType.Map.ordinal -> "Name-Value List"
        else -> MsgPackType.Unknown.toString()
    }
}

fun Field.keys(): List<String> {
    return when(this.type().toInt()) {
        MsgPackType.Map.ordinal -> this.map.keys("").sorted()
        MsgPackType.Array.ordinal -> (0 until this.array.keySizeAt("")).toList().map { it.toString() }
        else -> listOf()
    }
}

fun Field.value(): String {
    return when (this.type().toInt()) {
        MsgPackType.String.ordinal -> this.string
        MsgPackType.Int.ordinal -> this.int.toString()
        MsgPackType.Int8.ordinal -> this.int8.toString()
        MsgPackType.Int16.ordinal -> this.int16.toString()
        MsgPackType.Int32.ordinal -> this.int32.toString()
        MsgPackType.Int64.ordinal -> this.int64.toString()
        MsgPackType.Float32.ordinal -> this.float32.toString()
        MsgPackType.Float64.ordinal -> this.float64.toString()
        MsgPackType.Bool.ordinal -> this.bool.toString()
        MsgPackType.Nil.ordinal -> "null"
        MsgPackType.Time.ordinal -> Instant.ofEpochMilli(this.time).toString()
        else -> MsgPackType.Unknown.toString()
    }
}

fun Field.setValue(type:Int, value:String) {
    when (type) {
        MsgPackType.String.ordinal -> this.string = value
        MsgPackType.Int.ordinal -> this.int = value.toLongOrNull() ?: 0
        MsgPackType.Int8.ordinal -> this.int8 = value.toByteOrNull() ?: 0
        MsgPackType.Int16.ordinal -> this.int16 = value.toShortOrNull() ?: 0
        MsgPackType.Int32.ordinal -> this.int32 = value.toIntOrNull() ?: 0
        MsgPackType.Int64.ordinal -> this.int64 = value.toLongOrNull() ?: 0
        MsgPackType.Float32.ordinal -> this.float32 = value.toFloatOrNull() ?: 0.0f
        MsgPackType.Float64.ordinal -> this.float64 = value.toDoubleOrNull() ?: 0.0
        MsgPackType.Bool.ordinal -> this.bool = value.toBoolean()
        MsgPackType.Nil.ordinal -> {}
        MsgPackType.Time.ordinal -> this.time = Instant.parse(value).toEpochMilli()
        else -> {}
    }
}
fun Field.setValue(value:String) {
    setValue(this.type().toInt(), value)
}

fun Field.typeHintText(): String{
    return when (this.type().toInt()) {
        MsgPackType.String.ordinal -> "This is a text value. Any reasonable text should be ok"
        MsgPackType.Int.ordinal -> "Integer value. Valid values are from " + Int.MIN_VALUE + " to " + Int.MAX_VALUE
        MsgPackType.Int8.ordinal -> "8-bit integer value. Valid values are from " + Byte.MIN_VALUE + " to " + Byte.MAX_VALUE
        MsgPackType.Int16.ordinal -> "16-bit integer value. Valid values are from " + Short.MIN_VALUE + " to " + Short.MAX_VALUE
        MsgPackType.Int32.ordinal -> "32-bit integer value. Valid values are from " + Int.MIN_VALUE + " to " + Int.MAX_VALUE
        MsgPackType.Int64.ordinal -> "64-bit integer value. Valid values are from " + Long.MIN_VALUE + " to " + Long.MAX_VALUE
        MsgPackType.Float32.ordinal -> "32-bit floating point number value. Valid values are from " + Float.MIN_VALUE + " to " + Float.MAX_VALUE
        MsgPackType.Float64.ordinal -> "64-bit floating point number value. Valid values are from " + Double.MIN_VALUE + " to " + Double.MAX_VALUE
        MsgPackType.Bool.ordinal -> "Boolean value. Valid values are true or false"
        MsgPackType.Nil.ordinal -> "Null value (no value). Currently, no there is no support for changing null values."
        MsgPackType.Time.ordinal -> "Time value. Formatted as: Year-Month-DayTHours:Minutes:Seconds.MillisecondsZ where Month, Day, Hours, Minutes, and Seconds must be two digits. Lead with a zero for single-digit values."
        else -> "Unknown value. This value cannot be changed."
    }
}