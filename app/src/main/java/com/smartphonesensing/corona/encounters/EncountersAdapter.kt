package com.smartphonesensing.corona.encounters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.smartphonesensing.corona.databinding.EncountersListItemBinding


class EncountersAdapter(
    val clickListener: EncounterListener) :
        ListAdapter<ContactItem, RecyclerView.ViewHolder>(ItemDiffCallback()) {

//    private val adapterScope = CoroutineScope(Dispatchers.Default)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(getItem(position), clickListener)
        }
    }

    class ViewHolder private constructor(val binding: EncountersListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(encounterItem: ContactItem, clickListener: EncounterListener) {
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

class ItemDiffCallback : DiffUtil.ItemCallback<ContactItem>() {

    override fun areItemsTheSame(oldItem: ContactItem, newItem: ContactItem): Boolean {
        return oldItem.contact.ephId == newItem.contact.ephId
    }

    override fun areContentsTheSame(oldItem: ContactItem, newItem: ContactItem): Boolean {
        return oldItem.contact.ephId == newItem.contact.ephId &&
                oldItem.avgAttenuationLevelInt == newItem.avgAttenuationLevelInt &&
                oldItem.starttimeString == newItem.starttimeString &&
                oldItem.endtimeString == newItem.endtimeString
    }
}

class EncounterListener(val clickListener: (itemId: ContactItem) -> Unit) {
    fun onClick(encounterItem: ContactItem) = clickListener(encounterItem)
}

