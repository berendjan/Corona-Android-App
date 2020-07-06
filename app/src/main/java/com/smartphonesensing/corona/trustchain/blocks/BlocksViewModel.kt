package com.smartphonesensing.corona.trustchain.blocks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import nl.tudelft.ipv8.android.IPv8Android
import nl.tudelft.ipv8.attestation.trustchain.TrustChainBlock
import nl.tudelft.trustchain.common.util.TrustChainHelper
import nl.tudelft.ipv8.util.toHex
import org.dpppt.android.sdk.internal.crypto.SKList

class BlocksViewModel : ViewModel() {

    private val _blocks = MutableLiveData<List<BlockItem>>()
    val blocks : LiveData<List<BlockItem>>
        get() = _blocks

    private val _blockselected = MutableLiveData<BlockItem>()
    val blockselected : LiveData<BlockItem>
        get() = _blockselected

    val trustChainHelper = TrustChainHelper(IPv8Android.getInstance().getOverlay()!!)

    fun updateBlocks(blocks: List<TrustChainBlock>) {
        val newBlocks: MutableList<BlockItem> = mutableListOf()
        val indexMap: MutableMap<String, Int> = mutableMapOf()
        for (block : TrustChainBlock in blocks) {
            if (block.isProposal) {
                newBlocks.add(BlockItem(block, null))
                indexMap[block.blockId] = newBlocks.size - 1

            }
        }
        for (block : TrustChainBlock in blocks) {
            if (block.isAgreement) {
                if (indexMap.containsKey(block.linkedBlockId)) {
                    newBlocks[indexMap.getValue(block.linkedBlockId)].addAgreementBlock(block)
                }
            }
        }
        _blocks.value = newBlocks
    }

    fun updateSelectedBlock(block: BlockItem) {
        _blockselected.value = block
    }

    fun signBlock(block: BlockItem) {
        block.proposalBlock.let {
            block.signable = false
            trustChainHelper.createCoronaAgreementBlock(it, it.transaction)
        }
    }

}

class BlockItem(
    var proposalBlock: TrustChainBlock,
    var agreementBlock: TrustChainBlock?
) {
    val lengthString = 7
    val proposalBlockId: String = proposalBlock.blockId.substring(0..lengthString)
    val proposalPeer: String = proposalBlock.publicKey.toHex().substring(0..lengthString)
    val type: String = proposalBlock.type
    val proposalPeerSignature: String = proposalBlock.signature.toHex().substring(0..lengthString)
    val proposalPeerHash: String = proposalBlock.calculateHash().toHex().substring(0..lengthString)
    val proposalPeerPreviousHash: String = proposalBlock.previousHash.toHex().substring(0..lengthString)

    var blockStatus: String = proposalBlock.let { agreementBlock?.let { "Full block" }
        ?: "Halfblock" }

    val trustChainHelper = TrustChainHelper(IPv8Android.getInstance().getOverlay()!!)
    var signable: Boolean = proposalBlock.linkPublicKey.contentEquals(trustChainHelper.getMyPublicKey()) && agreementBlock == null


    fun addAgreementBlock(block: TrustChainBlock) {
        agreementBlock = block
        val agreementBlockId: String = agreementBlock?.blockId?.substring(0..lengthString) ?: ""
        blockStatus = proposalBlock.let { agreementBlock?.let { "Full block" }
            ?: "Halfblock" }.toString() ?: "Halfblock"
        val agreementPeer: String = agreementBlock?.publicKey?.toHex()?.substring(0..lengthString) ?: ""
        val agreementPeerSignature: String = agreementBlock?.signature?.toHex()?.substring(0..lengthString) ?: ""
        val agreementPeerHash: String = agreementBlock?.calculateHash()?.toHex()?.substring(0..lengthString) ?: ""
        val agreementPeerPreviousHash: String = agreementBlock?.previousHash?.toHex()?.substring(0..lengthString) ?: ""
        val trustChainHelper = TrustChainHelper(IPv8Android.getInstance().getOverlay()!!)
        signable = proposalBlock.linkPublicKey.contentEquals(trustChainHelper.getMyPublicKey()) && agreementBlock == null
    }
}