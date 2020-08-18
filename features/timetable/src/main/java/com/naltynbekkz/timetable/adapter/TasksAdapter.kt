package com.naltynbekkz.timetable.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.naltynbekkz.timetable.databinding.HeaderTaskBinding
import com.naltynbekkz.timetable.databinding.ItemTaskBinding

class TasksAdapter(
    private val tasks: ArrayList<com.naltynbekkz.timetable.model.Occurrence>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val HEADER = 0
        const val ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM) {
            ItemViewHolder(
                ItemTaskBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            HeaderViewHolder(
                HeaderTaskBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(tasks[position])
        }
    }

    override fun getItemCount() = if (tasks.isEmpty()) 0 else tasks.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position == 1) HEADER else ITEM
    }

    class ItemViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: com.naltynbekkz.timetable.model.Occurrence) {
            binding.task = task
        }
    }

    class HeaderViewHolder(binding: HeaderTaskBinding) : RecyclerView.ViewHolder(binding.root)
}