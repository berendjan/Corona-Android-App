package com.smartphonesensing.corona.trustchain
//
//import nl.tudelft.ipv8.messaging.Deserializable
//import nl.tudelft.ipv8.messaging.Serializable
//import nl.tudelft.ipv8.messaging.deserializeVarLen
//import nl.tudelft.ipv8.messaging.serializeVarLen
//import org.dpppt.android.sdk.internal.crypto.SKList
//
//class CoronaPayload (
//    val publicKey: ByteArray,
//    val SKList: SKList,
//    val type: Type
//) : Serializable {
//    override fun serialize(): ByteArray {
//        return serializeVarLen(publicKey) +
//                serializeVarLen(type.toString().toByteArray(Charsets.UTF_8))
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as CoronaPayload
//
//        if (!publicKey.contentEquals(other.publicKey)) return false
//        if (type != other.type) return false
//
//        return true
//    }
//
//    companion object Deserializer : Deserializable<TradePayload> {
//        override fun deserialize(buffer: ByteArray, offset: Int): Pair<TradePayload, Int> {
//            var localOffset = 0
//            val (publicKey, publicKeySize) = deserializeVarLen(buffer, offset + localOffset)
//            localOffset += publicKeySize
//            val (askCurrency, askCurrencySize) = deserializeVarLen(buffer, offset + localOffset)
//            localOffset += askCurrencySize
//            val (paymentCurrency, paymentCurrencySize) = deserializeVarLen(buffer, offset + localOffset)
//            localOffset += paymentCurrencySize
//            val (amount, amountSize) = deserializeVarLen(buffer, offset + localOffset)
//            localOffset += amountSize
//            val (price, priceSize) = deserializeVarLen(buffer, offset + localOffset)
//            localOffset += priceSize
//            val (type, typeSize) = deserializeVarLen(buffer, offset + localOffset)
//            localOffset += typeSize
//            val payload = CoronaPayload(
//                publicKey,
//                Currency.valueOf(askCurrency.toString(Charsets.UTF_8)),
//                Currency.valueOf(paymentCurrency.toString(Charsets.UTF_8)),
//                amount.toString(Charsets.UTF_8).toDouble(),
//                price.toString(Charsets.UTF_8).toDouble(),
//                Type.valueOf(type.toString(Charsets.UTF_8))
//            )
//            return Pair(payload, localOffset)
//        }
//    }
//
//    enum class Type {
//        ASK,
//        BID
//    }
//}
//
//class StringMessagePayload(val message: String) : Serializable {
//
//    override fun serialize(): ByteArray {
//        val m = "#!#$message"
//        return m.toByteArray()
//    }
//
//    companion object Deserializer : Deserializable<MyMessage> {
//        override fun deserialize(buffer: ByteArray, offset: Int): Pair<MyMessage, Int> {
//            val m = buffer.toString(Charsets.UTF_8)
//            return Pair(MyMessage(m.substringAfter("#!#")), buffer.size)
//        }
//    }
//}