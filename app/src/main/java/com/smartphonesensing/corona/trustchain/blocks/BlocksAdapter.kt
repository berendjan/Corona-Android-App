package com.smartphonesensing.corona.trustchain.blocks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smartphonesensing.corona.databinding.TrustchainBlocksListItemBinding
import com.smartphonesensing.corona.trustchain.peers.PeerListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class BlocksAdapter(
    val clickListener: BlockListener) :
    ListAdapter<BlockItem, RecyclerView.ViewHolder>(ItemDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(getItem(position), clickListener)
        }
    }

    class ViewHolder private constructor(val binding: TrustchainBlocksListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(block: BlockItem, clickListener: BlockListener) {
            binding.block = block
            binding.clickListener = clickListener
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TrustchainBlocksListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }
}

class ItemDiffCallback : DiffUtil.ItemCallback<BlockItem>() {

    override fun areItemsTheSame(oldItem: BlockItem, newItem: BlockItem): Boolean {
        return oldItem.proposalBlock?.blockId == newItem.proposalBlock?.blockId &&
                oldItem.agreementBlock?.blockId == newItem.agreementBlock?.blockId
    }

    override fun areContentsTheSame(oldItem: BlockItem, newItem: BlockItem): Boolean {
        return oldItem.proposalBlock?.transaction == newItem.proposalBlock?.transaction &&
                oldItem.agreementBlock?.transaction == newItem.agreementBlock?.transaction
    }
}

class BlockListener(val clickListener: (itemId: BlockItem) -> Unit, val signListener: (itemId: BlockItem) -> Unit) {
    fun onClick(block: BlockItem) = clickListener(block)
    fun onSign(block: BlockItem) = signListener(block)
}

