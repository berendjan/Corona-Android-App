package nl.tudelft.trustchain.common.util

import android.widget.Toast
import com.smartphonesensing.corona.trustchain.CoronaPayload
import com.smartphonesensing.corona.trustchain.MyMessage
import nl.tudelft.ipv8.Peer
import nl.tudelft.ipv8.attestation.trustchain.TrustChainBlock
import nl.tudelft.ipv8.attestation.trustchain.TrustChainCommunity
import nl.tudelft.ipv8.attestation.trustchain.TrustChainTransaction
import nl.tudelft.ipv8.attestation.trustchain.store.UserInfo
import nl.tudelft.ipv8.messaging.Serializable
import nl.tudelft.ipv8.util.hexToBytes
import nl.tudelft.ipv8.util.toHex
import org.dpppt.android.sdk.internal.crypto.SKList
import org.dpppt.android.sdk.internal.util.DayDate

/**
 * A helper class for interacting with TrustChain.
 */
class TrustChainHelper(
    private val trustChainCommunity: TrustChainCommunity
) {
    /**
     * Returns a list of users and their chain lengths.
     */
    fun getUsers(): List<UserInfo> {
        return trustChainCommunity.database.getUsers()
    }

    /**
     * Returns the number of blocks stored for the given public key.
     */
    fun getStoredBlockCountForUser(publicKeyBin: ByteArray): Long {
        return trustChainCommunity.database.getBlockCount(publicKeyBin)
    }

    /**
     * Returns a peer by its public key if found.
     */
    fun getPeerByPublicKeyBin(publicKeyBin: ByteArray): Peer? {
        return trustChainCommunity.network.getVerifiedByPublicKeyBin(publicKeyBin)
    }

    /**
     * Crawls the chain of the specified peer.
     */
    suspend fun crawlChain(peer: Peer) {
        trustChainCommunity.crawlChain(peer)
    }


//    /**
//     * Creates a new proposal block of type "accept_ask_block", using a float as transaction amount.
//     */
//    fun createAcceptTxProposalBlock(
//        primaryCurrency: Currency,
//        secondaryCurrency: Currency,
//        amount: Float?,
//        price: Float?,
//        type: TradePayload.Type,
//        publicKey: ByteArray
//    ): TrustChainBlock {
//        val blockType = "demo_tx_block"
//        val transaction = mapOf(
//            "From" to primaryCurrency.toString(),
//            "Amount from" to amount.toString(),
//            "To" to secondaryCurrency.toString(),
//            "Amount to" to price.toString()
//            "type" to type.toString()
//        )
//        return trustChainCommunity.createProposalBlock(blockType, transaction, publicKey)
//    }

    /**
     * Creates a new proposal block of type "demo_tx_block", using a float as transaction amount.
     */
    fun createOfflineTxProposalBlock(amount: Float, publicKey: ByteArray): TrustChainBlock {
        val blockType = "demo_tx_block"
        val transaction = mapOf("amount" to amount, "offline" to true)
        return trustChainCommunity.createProposalBlock(blockType, transaction, publicKey)
    }

    /**
     * Creates a new proposal block of type "demo_tx_block", using a float as transaction amount.
     */
    fun createTxProposalBlock(amount: Float?, publicKey: ByteArray): TrustChainBlock {
        val blockType = "demo_tx_block"
        val transaction = mapOf("amount" to amount)
        return trustChainCommunity.createProposalBlock(blockType, transaction, publicKey)
    }

    /**
     * Creates an agreement block to a specified proposal block, using a custom transaction.
     */
    fun createAgreementBlock(
        link: TrustChainBlock,
        transaction: TrustChainTransaction
    ): TrustChainBlock {
        return trustChainCommunity.createAgreementBlock(link, transaction)
    }

    /**
     * Returns a list of blocks in which the specified user is participating as a sender or
     * a receiver.
     */
    fun getChainByUser(publicKeyBin: ByteArray): List<TrustChainBlock> {
        return trustChainCommunity.database.getMutualBlocks(publicKeyBin, 1000)
    }

    fun getBlocksByType(type: String): List<TrustChainBlock> {
        return trustChainCommunity.database.getBlocksWithType(type)
    }

    /**
     * Returns public key of self
     */
    fun getMyPublicKey(): ByteArray {
        return trustChainCommunity.myPeer.publicKey.keyToBin()
    }

    /**
     * get Peers
     */
    fun getPeers() : List<Peer> {
        return trustChainCommunity.getPeers()
    }

    /**
     * Send message to peer
     */
    fun sendMessageToPeer(peer: Peer, message: String) {
        trustChainCommunity.broadcastPacketToPeer(peer, MessageId.STRING_MESSAGE, MyMessage(message))
    }

    /**
     * addMessageListener
     */
    fun addMessageListener(listener: TrustChainCommunity.GeneralPacketListener) {
        trustChainCommunity.addStringMessageListener(listener)
    }

    /**
     * Send SKList to peer
     */
    fun sendSKListToPeer(peer: Peer, SKList: SKList) {
        trustChainCommunity.broadcastPacketToPeer(peer, MessageId.CORONA_PACKET, CoronaPayload(SKList))
    }

    /**
     * add SKList packet listener
     */
    fun addSKListPacketListener(listener: TrustChainCommunity.GeneralPacketListener) {
        trustChainCommunity.addCoronaPacketListener(listener)
    }



    /**
     * Creates a new proposal block, using a text message as the transaction content.
     * Optionally, the blockType can be passed as an argument. Default value is "demo_block".
     */
    fun createDemoProposalBlock(
        message: String,
        publicKey: ByteArray,
        blockType: String = DEMO_BLOCK_TYPE
    ): TrustChainBlock {
        val transaction = mapOf("message" to message)
        return trustChainCommunity.createProposalBlock(blockType, transaction, publicKey)
    }

    /**
     * Create proposal block with SKList
     */
    fun createCoronaProposalBlock(
        SKList: SKList,
        publicKey: ByteArray,
        blockType: String = DEMO_BLOCK_TYPE
    ): TrustChainBlock {

        val transaction = mapOf(
            "SKList" to SKListToStringSKList(SKList)
        )
        return trustChainCommunity.createProposalBlock(blockType, transaction, publicKey)
    }

    private fun SKListToStringSKList(SKList: SKList) : ArrayList<Map<String, String>> {
        val stringSKList = arrayListOf<Map<String, String>>()
        for (pair: android.util.Pair<DayDate, ByteArray> in SKList) {
            stringSKList.add(mapOf(Pair(pair.first.formatAsString(), pair.second.toHex())))
        }
        return stringSKList
    }

    private fun stringSKListToSKList(stringSKList: ArrayList<Map<String, String>>) : SKList {
        val SKList = SKList()
        for (pair: Map<String, String> in stringSKList) {
            for ((key : String, value : String) in pair) {
                SKList.add(android.util.Pair(DayDate(key), value.hexToBytes()))
            }
        }
        return SKList
    }

    companion object {
        private const val PREF_PRIVATE_KEY = "private_key"
        private const val DEMO_BLOCK_TYPE = "demo_block"
        private const val CORONA_BLOCK_TYPE = "corona_block"
    }

    object MessageId {
        const val HALF_BLOCK = 1
        const val CRAWL_REQUEST = 2
        const val CRAWL_RESPONSE = 3
        const val HALF_BLOCK_PAIR = 4
        const val HALF_BLOCK_BROADCAST = 5
        const val HALF_BLOCK_PAIR_BROADCAST = 6
        const val EMPTY_CRAWL_RESPONSE = 7
        const val STRING_MESSAGE = 8
        const val CORONA_PACKET = 9
        const val GENERAL_PACKET = 10
    }
}
