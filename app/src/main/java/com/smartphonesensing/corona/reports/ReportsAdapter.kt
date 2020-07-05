package com.smartphonesensing.corona.reports

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.smartphonesensing.corona.databinding.ReportsListItemBinding

class ReportsAdapter(
    val clickListener: ReportsListener) :
    ListAdapter<ReportItem, RecyclerView.ViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(getItem(position), clickListener)
        }
    }

    class ViewHolder private constructor(val binding: ReportsListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(report: ReportItem, clickListener: ReportsListener) {
            binding.report = report
            binding.clickListener = clickListener
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ReportsListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }
}

class ItemDiffCallback : DiffUtil.ItemCallback<ReportItem>() {

    override fun areItemsTheSame(oldItem: ReportItem, newItem: ReportItem): Boolean {
        return oldItem.contact.ephId == newItem.contact.ephId
    }

    override fun areContentsTheSame(oldItem: ReportItem, newItem: ReportItem): Boolean {
        return oldItem.contact.ephId == newItem.contact.ephId
    }
}

class ReportsListener(val clickListener: (itemId: ReportItem) -> Unit) {
    fun onClick(report: ReportItem) = clickListener(report)
}

