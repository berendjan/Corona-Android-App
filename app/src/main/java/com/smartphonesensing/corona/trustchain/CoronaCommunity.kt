package com.smartphonesensing.corona.trustchain
//
//import android.util.Log
//import android.widget.Toast
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.smartphonesensing.corona.trustchain.peers.PeerListItem
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import nl.tudelft.ipv8.Community
//import nl.tudelft.ipv8.IPv4Address
//import nl.tudelft.ipv8.Peer
//import nl.tudelft.ipv8.android.IPv8Android
//import nl.tudelft.ipv8.attestation.trustchain.TrustChainBlock
//import nl.tudelft.ipv8.attestation.trustchain.TrustChainCommunity
//import nl.tudelft.ipv8.messaging.Deserializable
//import nl.tudelft.ipv8.messaging.Packet
//import nl.tudelft.ipv8.messaging.Serializable
//import nl.tudelft.ipv8.messaging.payload.IntroductionResponsePayload
//import java.util.*
//
//private const val MESSAGE_ID_HELLO = 1
//private const val BLOCK_TYPE = "demo_block"
//
//class CoronaCommunity : Community() {
//    override val serviceId = "02313685c1912a141279f8248fc8db5899c12346"
//
//    init {
//        messageHandlers[MESSAGE_ID_HELLO] = ::onHelloMessage
//    }
//
//    private val _message = MutableLiveData<String>()
//    val message: LiveData<String>
//        get() = _message
//
//    val discoveredAddressesContacted: MutableMap<IPv4Address, Date> = mutableMapOf()
//    val lastTrackerResponses = mutableMapOf<IPv4Address, Date>()
//
//    override fun walkTo(address: IPv4Address) {
//        super.walkTo(address)
//
//        discoveredAddressesContacted[address] = Date()
//    }
//
//    // Retrieve the trustchain community
//    private fun getTrustChainCommunity(): TrustChainCommunity {
//        return IPv8Android.getInstance().getOverlay()
//            ?: throw IllegalStateException("TrustChainCommunity is not configured")
//    }
//
//    override fun onIntroductionResponse(peer: Peer, payload: IntroductionResponsePayload) {
//        super.onIntroductionResponse(peer, payload)
//
//        if (peer.address in DEFAULT_ADDRESSES) {
//            lastTrackerResponses[peer.address] = Date()
//        }
//    }
//
//    /**
//     * Sending a packet to arg peer over network
//     */
//    fun broadcastHelloMessage(peer : Peer, message: String) {
//        val packet = serializePacket(MESSAGE_ID_HELLO, MyMessage(message))
//        send(peer.address, packet)
//    }
//    /**
//     * Receiving a packet from peer over network
//     */
//    private fun onHelloMessage(packet: Packet) {
//        val (peer: Peer, payload: MyMessage) = packet.getAuthPayload(MyMessage.Deserializer)
//        scope.launch{
//
//            _message.value = "Received message '${payload.message}' from peer ${peer.mid}"
//        }
//    }
//
//
//    /**
//     * Step 1 creating new proposal block
//     * Next steps in MainApplication.kt
//     *
//     * The method creates a TrustChainBlock, stores it in the local database, and broadcasts it to all peers.
//     */
//    fun createAndSendProposalBlock(peer: Peer) {
//        val ipv8 = IPv8Android.getInstance()
//        val trustchain = ipv8.getOverlay<TrustChainCommunity>()!!
//
//        val transaction = mapOf("messageKey" to "messageValue")
//        val publicKey = peer.publicKey.keyToBin()
//        Log.d("TrustChainDemo", "Called create And Send Proposal Block")
//        logAllMyBLocks(trustchain, publicKey)
//        trustchain.createProposalBlock(BLOCK_TYPE, transaction, publicKey)
//        logAllMyBLocks(trustchain, publicKey)
//    }
//
//    fun logAllMyBLocks(trustchain : TrustChainCommunity, publicKey : ByteArray) {
//
//        val latestBlock = trustchain.database.getLatest(publicKey)
//
//        latestBlock?.let {
////            val allblocks = trustchain.database.getAllBlocks()
//            val peerblocks = trustchain.database.getMutualBlocks(publicKey, 1000)
//            val myKey = myPeer.publicKey.keyToBin()
//            val myblocks = trustchain.database.getMutualBlocks(myKey)
//            for (block: TrustChainBlock in peerblocks) {
//                Log.d("TrustChainDemo", "Linked block to current peer: ${block.blockId}, \nis genesis: ${block.isGenesis}, \nis agreement ${block.isAgreement}\n previous: ${block.previousHash}")
//            }
//            for (block: TrustChainBlock in myblocks) {
//                Log.d("TrustChainDemo", "Linked block to current peer: ${block.blockId}, \nis genesis: ${block.isGenesis}, \nis agreement ${block.isAgreement}\n previous: ${block.previousHash}")
//            }
//        }
//
//    }
//
//}
//
//class MyMessage(val message: String) : Serializable {
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