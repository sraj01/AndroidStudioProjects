package com.example.tasklyy.AddTaskScreen

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tasklyy.Local.DB.taskData.Task
import com.example.tasklyy.R
import com.example.tasklyy.databinding.AddTaskScreenBinding // Use your actual binding class name
import com.example.tasklyy.MapLocationPickerScreen // Import your MapLocationPickerActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddTaskScreen : AppCompatActivity() {

    private lateinit var binding: AddTaskScreenBinding
    private val calendar = Calendar.getInstance()
    private val viewModel: AddTaskViewModel by viewModels()

    // Selected values for the task
    private var selectedAssigneeId: Int? = null
    private var selectedPriority: Int = 1 // default medium
    private var selectedStatus: String = "Todo" // default status

    // --- TASK'S LOCATION ADDRESS ---
    private var selectedAddress: String? = null

    // ActivityResultLauncher for MapLocationPickerActivity
    private val mapLocationPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                selectedAddress = data?.getStringExtra(MapLocationPickerScreen.EXTRA_ADDRESS)

                binding.tvSelectedLocation.text = selectedAddress ?: getString(R.string.location)
                Log.d("AddTaskScreen", "Location selected: $selectedAddress")
            } else {
                Log.d("AddTaskScreen", "Location picking cancelled or failed for AddTaskScreen.")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddTaskScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup UI components
        setupPriorityRadioGroup()
        setupStatusSpinner()
        setupAssigneeSpinner()
        setupDatePicker()
        setupLocationPickerButton() // Renamed for clarity
        setupSaveButton()

        // Initialize location text view
        binding.tvSelectedLocation.text = getString(R.string.location)
    }

    private fun setupPriorityRadioGroup() {
        binding.rgPriority.check(R.id.rbMedium) // Default check
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
        val statuses = resources.getStringArray(R.array.status_array).toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statuses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spStatus.adapter = adapter
        // Set default selection if needed, e.g., to "Todo"
        val todoPosition = statuses.indexOf("Todo")
        if (todoPosition >= 0) {
            binding.spStatus.setSelection(todoPosition)
        }

        binding.spStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedStatus = statuses[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedStatus = statuses[0] // Default if nothing selected
            }
        }
    }

    private fun setupAssigneeSpinner() {
        viewModel.allUsersForAssigneeFilter.observe(this) { userList ->
            val mutableUserList = userList?.toMutableList() ?: mutableListOf()

            val userNames = mutableUserList.map { it.userName }.toMutableList()
            // Add an "Unassigned" option at the beginning
            val unassignedString = getString(R.string.unassigned)
            userNames.add(0, unassignedString)

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, userNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spAssignee.adapter = adapter

            binding.spAssignee.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    if (position == 0) { // "Unassigned" selected
                        selectedAssigneeId = null
                    } else {
                        // Adjust index because "Unassigned" was added at the start
                        selectedAssigneeId = mutableUserList.getOrNull(position - 1)?.userId
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    selectedAssigneeId = null
                }
            }
        }
    }

    private fun setupDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        binding.dateInputLayout.setEndIconOnClickListener {
            DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).apply {
                datePicker.minDate = System.currentTimeMillis() - 1000 // Allow today
            }.show()
        }
        binding.dateEditText.setOnClickListener { // Also allow clicking the EditText itself
            binding.dateInputLayout.performClick()
        }
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy" // Define your date format
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.dateEditText.setText(sdf.format(calendar.time))
    }

    private fun setupLocationPickerButton() {
        binding.btnSelectLocation.setOnClickListener {
            val intent = Intent(this, MapLocationPickerScreen::class.java)
            // Pass the current address (if any) to the map picker as a hint
            selectedAddress?.let { addr -> intent.putExtra(MapLocationPickerScreen.INITIAL_ADDRESS_HINT, addr) }
            mapLocationPickerLauncher.launch(intent)
        }
    }

    private fun setupSaveButton() {
        binding.btnSubmit.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val dueDate = binding.dateEditText.text.toString().trim()

            if (title.isEmpty()) {
                binding.etTitle.error = "Title cannot be empty"
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.etTitle.error = null // Clear error

            if (dueDate.isEmpty()) {
                // You might want an error on the date field itself
                Toast.makeText(this, "Please select a due date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Add more validation as needed for other fields

            val task = Task(
                title = title,
                description = description,
                taskPriority = selectedPriority,
                assigneeId = selectedAssigneeId,
                dueDate = dueDate,
                taskStatus = selectedStatus,
                locationAddress = selectedAddress // Save the selected address
            )

            viewModel.saveTask(task)
            Toast.makeText(this, "Task saved!", Toast.LENGTH_SHORT).show()
            finish() // Close the activity
        }
    }
}
