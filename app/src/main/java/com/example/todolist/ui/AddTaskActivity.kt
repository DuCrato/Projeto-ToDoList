package com.example.todolist.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.databinding.ActivityAddTaskBinding
import com.example.todolist.datesource.TaskDateSource
import com.example.todolist.extensions.format
import com.example.todolist.extensions.text
import com.example.todolist.model.Task
import com.google.android.material.datepicker.MaterialDatePicker.Builder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(TASK_ID)) {

            val taskId = intent.getIntExtra(TASK_ID, 0)

            TaskDateSource.findById(taskId)?.let {

                binding.editTitle.text = it.title
                binding.editDescription.text = it.description
                binding.editDate.text = it.date
                binding.editHour.text = it.hour

            }
        }

        insertListenes()
    }

    private fun insertListenes() {

        binding.editDate.editText?.setOnClickListener {

            val datePicker = Builder.datePicker().build()

            datePicker.addOnPositiveButtonClickListener {

                val timeZone = TimeZone.getDefault()
                val offSet = timeZone.getOffset(Date().time) * -1

                binding.editDate.text = Date(it + offSet).format()
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
        }

        binding.editHour.editText?.setOnClickListener {

            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build()

            timePicker.addOnPositiveButtonClickListener {

                val hour = if (timePicker.hour in 0..9) "0${timePicker.hour}" else timePicker.hour
                val minute =
                    if (timePicker.minute in 0..9) "0${timePicker.minute}" else timePicker.minute
                binding.editHour.text = "${hour}:${minute}"
            }

            timePicker.show(supportFragmentManager, "TIME_PICKER_TAG")
        }

        binding.btnCancelTask.setOnClickListener { finish() }

        binding.btnCreateTask.setOnClickListener {

            val task = Task(
                title = binding.editTitle.text,
                description = binding.editDescription.text,
                date = binding.editDate.text,
                hour = binding.editHour.text,
                id = intent.getIntExtra(TASK_ID, 0)

            )

            TaskDateSource.insertTask(task)
            setResult(Activity.RESULT_OK)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }

    }

    companion object {
        const val TASK_ID = "task_id"
    }
}