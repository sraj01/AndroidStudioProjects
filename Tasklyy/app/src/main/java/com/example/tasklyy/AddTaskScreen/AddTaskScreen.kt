package com.example.tasklyy.AddTaskScreen

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tasklyy.AddTaskViewModel
import com.example.tasklyy.Local.DB.taskData.Task
import com.example.tasklyy.R
import com.example.tasklyy.databinding.AddTaskScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddTaskScreen : AppCompatActivity() {

    private lateinit var binding: AddTaskScreenBinding
    private val calendar = Calendar.getInstance()

    private val viewModel: AddTaskViewModel by viewModels()

    // selected values
    private var selectedAssigneeId: Int? = null
    private var selectedPriority: Int = 1   // default medium
    private var selectedStatus: String = "Todo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddTaskScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPriorityRadioGroup()
        setupStatusSpinner()
        setupAssigneeSpinner()
        setupDatePicker()
        setupSaveButton()
    }

    private fun setupPriorityRadioGroup() {
        binding.rgPriority.setOnCheckedChangeListener { _, checkedId ->
            selectedPriority = when (checkedId) {
                R.id.rbHigh -> 0
                R.id.rbMedium -> 1
                R.id.rbLow -> 2
                else -> 1
            }
        }
    }

    private fun setupStatusSpinner() {
        val statuses = listOf("Todo", "InProgress", "Done")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statuses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spStatus.adapter = adapter

        binding.spStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedStatus = statuses[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupAssigneeSpinner() {
        viewModel.users.observe(this) { userList ->
            if (userList.isEmpty()) {
                Toast.makeText(this, "No users found. Add users first!", Toast.LENGTH_SHORT).show()
                return@observe
            }

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                userList.map { it.userName }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spAssignee.adapter = adapter

            binding.spAssignee.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    selectedAssigneeId = userList[position].userId
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    selectedAssigneeId = null
                }
            }
        }
    }

    private fun setupDatePicker() {
        binding.dateInputLayout.setEndIconOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    calendar.set(selectedYear, selectedMonth, selectedDayOfMonth)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    binding.dateEditText.setText(dateFormat.format(calendar.time))
                },
                year,
                month,
                day
            )

            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }
    }

    private fun setupSaveButton() {
        binding.btnSubmit.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val dueDate = binding.dateEditText.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val task = Task(
                title = title,
                description = description,
                taskPriority = selectedPriority,
                assigneeId = selectedAssigneeId,
                dueDate = dueDate,
                taskStatus = selectedStatus
            )

            viewModel.saveTask(task)
            Toast.makeText(this, "Task saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}