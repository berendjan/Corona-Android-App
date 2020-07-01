package com.smartphonesensing.corona.trustchain.peers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smartphonesensing.corona.databinding.TrustchainPeersListItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class PeersAdapter(
    val clickListener: PeerListener) :
    ListAdapter<PeerListItem, RecyclerView.ViewHolder>(ItemDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(getItem(position), clickListener)
        }
    }

    class ViewHolder private constructor(val binding: TrustchainPeersListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(peer: PeerListItem, clickListener: PeerListener) {
            binding.peer = peer
            binding.clickListener = clickListener
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TrustchainPeersListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }
}

class ItemDiffCallback : DiffUtil.ItemCallback<PeerListItem>() {

    override fun areItemsTheSame(oldItem: PeerListItem, newItem: PeerListItem): Boolean {
        return oldItem.peer.mid == newItem.peer.mid
    }

    override fun areContentsTheSame(oldItem: PeerListItem, newItem: PeerListItem): Boolean {
        return oldItem.peer == newItem.peer
    }
}

class PeerListener(val clickListener: (itemId: PeerListItem) -> Unit) {
    fun onClick(peer: PeerListItem) = clickListener(peer)
}

