package com.example.todolist

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.network.TaskApi
import com.example.todolist.network.TaskProperty
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var mTaskList = mutableListOf<TaskProperty>()

    private val adapter = TaskListAdapter(mTaskList, this::onCheckItem, this::deleteTask)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
                showAddItemDialog {
                    if (it !== null && it.isNotBlank()) {
                       lifecycleScope.launch {
                           val newTask = TaskApi.retrofitService.addTask(TaskProperty("", false, it )).await()
                           mTaskList.add(0, newTask)
                           recyclerview.adapter?.notifyItemInserted(0)
                           recyclerview.smoothScrollToPosition(0)
                       }
                    }
                }
        }

        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            mTaskList.clear()
            mTaskList.addAll(TaskApi.retrofitService.getProperties().await())
            adapter.notifyDataSetChanged()
        }

    }

    private fun onCheckItem(position: Int, isChecked: Boolean) {
        val task = mTaskList[position].id
        if(isChecked) {
            lifecycleScope.launch {
                TaskApi.retrofitService.closeTask(task).await()
            }
            } else {
            lifecycleScope.launch {
                TaskApi.retrofitService.reOpenTask(task).await()
            }
        }
    }

    private fun deleteTask(position: Int, isChecked: Boolean) {
        val task: String = mTaskList[position].id
        if(isChecked) {
            lifecycleScope.launch {
                val response = TaskApi.retrofitService.deleteTasks(task).await()
                if (response.isSuccessful) {
                    mTaskList.removeAt(position)
                    adapter.notifyItemRemoved(position)
                } else {
                    Log.d("DELETE", "fail delete")
                }
            }
        } else {
            Log.d("delete", "peux pas delete car pas close")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun showAddItemDialog(onFinish: (String?) -> Unit ) {
        val editText = EditText(this)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Add a new task")
            .setMessage("What do you want to do next ?")
            .setView(editText)
            .setPositiveButton("Add") {_, _ -> onFinish(editText.text.toString()) }
            .setNegativeButton("Cancel") {_,_ -> onFinish(null) }
            .create()
        dialog.show()
    }
}