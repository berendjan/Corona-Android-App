package com.smartphonesensing.corona.trustchain

import nl.tudelft.ipv8.messaging.Deserializable
import nl.tudelft.ipv8.messaging.Serializable
import nl.tudelft.ipv8.messaging.deserializeVarLen
import nl.tudelft.ipv8.messaging.serializeVarLen
import nl.tudelft.ipv8.util.hexToBytes
import nl.tudelft.ipv8.util.toHex
import org.dpppt.android.sdk.internal.crypto.SKList
import org.dpppt.android.sdk.internal.util.DayDate

class CoronaPayload (
    val SKList: SKList
) : Serializable {

    /**
     * SKList : ArrayList<Pair<DayDate, byte[]>>
     *
     */
    override fun serialize(): ByteArray {

        var byteArray : ByteArray = serializeVarLen(SKList.size.toString().toByteArray(Charsets.UTF_8))
        for (pair : android.util.Pair<DayDate, ByteArray> in SKList) {
            byteArray += serializeVarLen(pair.first.formatAsString().toByteArray(Charsets.UTF_8))
            byteArray += serializeVarLen(pair.second.toHex().toByteArray(Charsets.UTF_8))
        }

        return byteArray
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CoronaPayload

        if (SKList.size != other.SKList.size) return false

        for (i : Int in 0 until SKList.size) {
            if (SKList[i].first != other.SKList[i].first) return false
            if (!SKList[i].second!!.contentEquals(other.SKList[i].second!!)) return false
        }

        return true
    }

    companion object Deserializer : Deserializable<CoronaPayload> {
        override fun deserialize(buffer: ByteArray, offset: Int): Pair<CoronaPayload, Int> {

            val newSKList = SKList()
            var localOffset = 0

            val (SKListSizeBytes, SKListSizeLength) = deserializeVarLen(buffer, offset + localOffset)
            val SKListSizeSize = SKListSizeBytes.toString(Charsets.UTF_8).toInt()
            localOffset += SKListSizeLength

            for (i in 0 until SKListSizeSize) {
                val (dayDateBytes, dayDateLength) = deserializeVarLen(buffer, offset + localOffset)
                val dayDate = DayDate(dayDateBytes.toString(Charsets.UTF_8))
                localOffset += dayDateLength

                val (byteArrayBytes, byteArrayLength) = deserializeVarLen(buffer, offset + localOffset)
                val byteArray = byteArrayBytes.toString(Charsets.UTF_8).hexToBytes()
                localOffset += byteArrayLength

                newSKList.add(android.util.Pair(dayDate, byteArray))
            }

            val payload = CoronaPayload(newSKList)

            return Pair(payload, localOffset)
        }
    }
}

/**
 * String Message Object
 */
class MyMessage(val message: String) : Serializable {
    override fun serialize(): ByteArray {
        val m = "#!#$message"
        return m.toByteArray()
    }

    companion object Deserializer : Deserializable<MyMessage> {
        override fun deserialize(buffer: ByteArray, offset: Int): Pair<MyMessage, Int> {
            val m = buffer.toString(Charsets.UTF_8)
            return Pair(MyMessage(m.substringAfter("#!#")), buffer.size)
        }
    }
}