package com.example.todolist

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import java.util.*
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import com.example.todolist.network.TaskProperty
import kotlin.reflect.jvm.internal.impl.util.Check

class TaskListAdapter(
    val mTaskList: List<TaskProperty>,
    val onCheckChange: (Int, Boolean) -> Unit,
    val deleteTask: (Int, Boolean) -> Unit
    ) : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.taskItemView.text = mTaskList.get(position).content
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListAdapter.TaskViewHolder {
        return TaskViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false))
    }

    override fun getItemCount(): Int {
        return mTaskList.size
    }


    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskItemView = itemView.findViewById<TextView>(R.id.task)
        val checkBox = itemView.findViewById<CheckBox>(R.id.task)
        val deleteItem = itemView.findViewById<ImageView>(R.id.delete_task_item)

        private var CheckBox.strikeThrough
            get() = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG > 0
            set(value) {
                paintFlags = if (value)
                    paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else
                    paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

        init {
            itemView.setOnClickListener { checkBox.isChecked = !checkBox.isChecked }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                deleteItem.visibility = if (checkBox.isChecked) View.VISIBLE else View.INVISIBLE
                onCheckChange(adapterPosition, isChecked)
                checkBox.strikeThrough = isChecked

                deleteItem.setOnClickListener{
                    deleteTask(adapterPosition, isChecked)
                }
            }
        }
    }
}