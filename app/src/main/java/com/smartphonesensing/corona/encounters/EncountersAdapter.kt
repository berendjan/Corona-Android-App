package com.smartphonesensing.corona.encounters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.smartphonesensing.corona.databinding.EncountersListItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class EncountersAdapter(
    val clickListener: EncounterListener) :
        ListAdapter<EncounterItem, RecyclerView.ViewHolder>(ItemDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(getItem(position), clickListener)
        }
    }

    class ViewHolder private constructor(val binding: EncountersListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(encounterItem: EncounterItem, clickListener: EncounterListener) {
            binding.encounterItem = encounterItem
            binding.clickListener = clickListener
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = EncountersListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }
}

class ItemDiffCallback : DiffUtil.ItemCallback<EncounterItem>() {

    override fun areItemsTheSame(oldItem: EncounterItem, newItem: EncounterItem): Boolean {
        return oldItem.identifier == newItem.identifier
    }

    override fun areContentsTheSame(oldItem: EncounterItem, newItem: EncounterItem): Boolean {
        return oldItem == newItem
    }
}

class EncounterListener(val clickListener: (itemId: EncounterItem) -> Unit) {
    fun onClick(encounterItem: EncounterItem) = clickListener(encounterItem)
}

