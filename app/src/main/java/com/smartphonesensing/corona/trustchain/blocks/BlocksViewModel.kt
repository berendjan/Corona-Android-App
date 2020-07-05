package com.smartphonesensing.corona.trustchain.blocks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import nl.tudelft.ipv8.android.IPv8Android
import nl.tudelft.ipv8.attestation.trustchain.TrustChainBlock
import nl.tudelft.trustchain.common.util.TrustChainHelper

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
        val newBlocksMap: MutableMap<String, TrustChainBlock?> = mutableMapOf()
        for (block : TrustChainBlock in blocks) {
            if (block.isProposal) {
                newBlocksMap[block.blockId] = block
            }
        }
        for (block : TrustChainBlock in blocks) {
            if (block.isAgreement) {
                newBlocksMap[block.linkedBlockId]?.let {
                    newBlocks.add(BlockItem(it, block))
                } ?: run {
                    newBlocks.add(BlockItem(null, block))
                }
            }
        }

        _blocks.value = newBlocks
    }

    fun updateSelectedBlock(block: BlockItem) {
        _blockselected.value = block
    }

    fun signBlock(block: BlockItem) {
        block.proposalBlock?.let {
            trustChainHelper.createCoronaAgreementBlock(it, it.transaction)
        }
    }

}

class BlockItem(
    val proposalBlock: TrustChainBlock?,
    val agreementBlock: TrustChainBlock?
)