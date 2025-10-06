package com.example.tasklyy.HomeScreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasklyy.AddTaskScreen.TaskAdapter
import com.example.tasklyy.Local.DB.UserEntity
import com.example.tasklyy.R
import com.example.tasklyy.AddTaskScreen.AddTaskViewModel
import com.example.tasklyy.AddTaskScreen.ActiveFilter
import com.example.tasklyy.TaskDetailsScreen.TaskDetailActivity
import com.example.tasklyy.databinding.FragmentBoardBinding
import com.google.android.material.chip.Chip
// Removed ChipGroup import as we are not using its listener directly for individual chip logic anymore
// import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BoardFragment : Fragment() {

    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddTaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter
    private var usersForAssigneePopup: List<UserEntity> = emptyList()

    companion object {
        const val STATUS_TODO = "Todo"
        const val STATUS_IN_PROGRESS = "InProgress"
        const val STATUS_DONE = "Done"
        const val ALL_ASSIGNEES_MENU_ID = -1
        const val ALL_STATUSES_MENU_ID = -2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFilterChipsListeners() // Renamed for clarity
        setupSearchView()
        observeViewModelData()
    }

    /*private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(emptyList()) { clickedTask ->
            Toast.makeText(
                requireContext(),
                "Task Clicked: ${clickedTask.title}",
                Toast.LENGTH_SHORT
            ).show()
            // Consider navigating to a task detail screen here
            // findNavController().navigate(BoardFragmentDirections.actionBoardFragmentToTaskDetailFragment(clickedTask.id))
        }
        binding.recyclerrview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }
    }*/
    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(requireContext()
                ,emptyList()) { clickedTask ->
            Log.d("BoardFragment", "Task clicked: ID ${clickedTask.id}, Title: ${clickedTask.title}")
            val intent = Intent(requireContext(), TaskDetailActivity::class.java).apply {
                putExtra(TaskDetailActivity.EXTRA_TASK_ID, clickedTask.id)
            }
            startActivity(intent)
        }
        binding.recyclerrview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }
    }
    private fun observeViewModelData() {
        viewModel.tasksToDisplay.observe(viewLifecycleOwner) { tasks ->
            tasks?.let {
                Log.d("BoardFragment", "TasksToDisplay Observer: Updating UI with ${it.size} tasks. ActiveFilter: ${viewModel.activeFilterType.value}, StatusArg: ${viewModel.statusFilterArgument.value}, AssigneeArg: ${viewModel.selectedAssigneeUserId.value}")
                taskAdapter.submitList(it)


                val currentSearchQuery = binding.SearchTitle.query?.toString()
                if (!currentSearchQuery.isNullOrEmpty()) {
                    taskAdapter.filter.filter(currentSearchQuery)
                }
            }
        }

        viewModel.allUsersForAssigneeFilter.observe(viewLifecycleOwner) { users ->
            usersForAssigneePopup = users ?: emptyList()
            Log.d("BoardFragment", "Fetched ${usersForAssigneePopup.size} users for assignee popup.")
            taskAdapter.setUserList(usersForAssigneePopup)
        }

        viewModel.activeFilterType.observe(viewLifecycleOwner) {
            Log.d("BoardFragment", "ActiveFilterType Observer: $it")
            updateChipStates()
        }
        viewModel.statusFilterArgument.observe(viewLifecycleOwner) {
            Log.d("BoardFragment", "StatusFilterArgument Observer: $it")
            updateChipStates()
        }
        viewModel.selectedAssigneeUserId.observe(viewLifecycleOwner) {
            Log.d("BoardFragment", "SelectedAssigneeUserId Observer: $it")
            updateChipStates()
        }
    }

    private fun updateChipStates() {
        Log.d("BoardFragment", "updateChipStates CALLED")
        val activeFilter = viewModel.activeFilterType.value
        val selectedStatus = viewModel.statusFilterArgument.value
        val selectedAssigneeId = viewModel.selectedAssigneeUserId.value

        Log.d("BoardFragment", "updateChipStates - Active: $activeFilter, Status: $selectedStatus, AssigneeID: $selectedAssigneeId")

        val shouldStatusBeChecked = (activeFilter == ActiveFilter.STATUS && selectedStatus != null)
        val shouldAssigneeBeChecked = (activeFilter == ActiveFilter.ASSIGNEE && selectedAssigneeId != null)
        val shouldDueDateBeChecked = (activeFilter == ActiveFilter.DUE_DATE)

        binding.chipStatus.isChecked = shouldStatusBeChecked
        binding.chipAssignee.isChecked = shouldAssigneeBeChecked
        binding.chipDuedate.isChecked = shouldDueDateBeChecked

        binding.chipStatus.text = if (shouldStatusBeChecked) selectedStatus else getString(R.string.status)
        binding.chipAssignee.text = if (shouldAssigneeBeChecked) {
            usersForAssigneePopup.find { it.userId == selectedAssigneeId }?.userName ?: getString(R.string.assignee) // Fallback if user not found
        } else {
            getString(R.string.assignee)
        }

        if (activeFilter == ActiveFilter.NONE) {

            Log.d("BoardFragment", "updateChipStates: ActiveFilter is NONE, chips should be unchecked and text reset by above logic.")
        }
    }

    private fun setupFilterChipsListeners() {
        binding.filterChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            Log.d("BoardFragment", "ChipGroup OnCheckedStateChangeListener - checkedIds: $checkedIds")
            if (binding.SearchTitle.query.isNotEmpty()) {
                binding.SearchTitle.setQuery("", false)
                taskAdapter.filter.filter("") // Re-apply empty filter to show current chip-filtered list
            }

            if (checkedIds.isEmpty()) {

                Log.d("BoardFragment", "ChipGroup: No chips checked, clearing all filters via ViewModel.")
                viewModel.clearAllFilters() // This will trigger observers and updateChipStates
            }
        }

        binding.chipStatus.setOnClickListener {
            Log.d("BoardFragment", "chipStatus Clicked. Current isChecked: ${binding.chipStatus.isChecked}")
            if (binding.SearchTitle.query.isNotEmpty()) {
                binding.SearchTitle.setQuery("", false)
                taskAdapter.filter.filter("")
            }
            showStatusSelectionPopup(binding.chipStatus)
        }

        binding.chipAssignee.setOnClickListener {
            Log.d("BoardFragment", "chipAssignee Clicked. Current isChecked: ${binding.chipAssignee.isChecked}")
            if (binding.SearchTitle.query.isNotEmpty()) {
                binding.SearchTitle.setQuery("", false)
                taskAdapter.filter.filter("")
            }
            showAssigneeSelectionPopup(binding.chipAssignee)
        }

        binding.chipDuedate.setOnClickListener {
            Log.d("BoardFragment", "chipDuedate Clicked. Current isChecked: ${binding.chipDuedate.isChecked}")
            if (binding.SearchTitle.query.isNotEmpty()) {
                binding.SearchTitle.setQuery("", false)
                taskAdapter.filter.filter("")
            }


            if (viewModel.activeFilterType.value == ActiveFilter.DUE_DATE) {
                Log.d("BoardFragment", "chipDuedate: DueDate filter was active, clearing it.")
                viewModel.clearDueDateFilter()
            } else {
                Log.d("BoardFragment", "chipDuedate: Applying DueDate filter.")
                viewModel.applyDueDateFilter()
            }

        }
    }

    private fun showStatusSelectionPopup(anchor: Chip) {
        Log.d("BoardFragment", "showStatusSelectionPopup For Anchor: ${anchor.text}")
        val popup = PopupMenu(requireContext(), anchor)
        popup.menu.add(0, ALL_STATUSES_MENU_ID, 0, getString(R.string.all_statuses))
        popup.menu.add(0, R.id.status_todo, 1, STATUS_TODO)

        popup.menu.add(0, R.id.status_inprogress, 2, STATUS_IN_PROGRESS)
        popup.menu.add(0, R.id.status_done, 3, STATUS_DONE)

        popup.setOnMenuItemClickListener { item ->
            Log.d("BoardFragment", "Status Popup MenuItem Clicked: ${item.title}")
            when (item.itemId) {
                ALL_STATUSES_MENU_ID -> viewModel.clearStatusFilter()
                R.id.status_todo -> viewModel.applyStatusFilter(STATUS_TODO)
                R.id.status_inprogress -> viewModel.applyStatusFilter(STATUS_IN_PROGRESS)
                R.id.status_done -> viewModel.applyStatusFilter(STATUS_DONE)
            }
            true
        }
        popup.setOnDismissListener {
            Log.d("BoardFragment", "Status Popup Dismissed. Current ViewModel state - ActiveFilter: ${viewModel.activeFilterType.value}, StatusArg: ${viewModel.statusFilterArgument.value}")

        }
        popup.show()
    }

    private fun showAssigneeSelectionPopup(anchor: Chip) {
        Log.d("BoardFragment", "showAssigneeSelectionPopup For Anchor: ${anchor.text}")
        if (usersForAssigneePopup.isEmpty()) {
            Toast.makeText(requireContext(), R.string.no_assignees_available, Toast.LENGTH_SHORT).show()

            viewModel.clearAssigneeFilter()
            return
        }

        val popup = PopupMenu(requireContext(), anchor)
        popup.menu.add(0, ALL_ASSIGNEES_MENU_ID, 0, getString(R.string.all_assignees_menu_item))
        usersForAssigneePopup.forEachIndexed { index, user ->

            popup.menu.add(0, user.userId, index + 1, user.userName)
        }

        popup.setOnMenuItemClickListener { item ->
            Log.d("BoardFragment", "Assignee Popup MenuItem Clicked: ${item.title}, ID: ${item.itemId}")
            val selectedUserId = if (item.itemId == ALL_ASSIGNEES_MENU_ID) null else item.itemId
            viewModel.applyAssigneeFilter(selectedUserId)
            true
        }
        popup.setOnDismissListener {
            Log.d("BoardFragment", "Assignee Popup Dismissed. Current ViewModel state - ActiveFilter: ${viewModel.activeFilterType.value}, AssigneeId: ${viewModel.selectedAssigneeUserId.value}")
        }
        popup.show()
    }

    private fun setupSearchView() {
        binding.SearchTitle.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("BoardFragment", "Search SUBMIT: $query")
                taskAdapter.filter.filter(query)
                binding.SearchTitle.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("BoardFragment", "Search CHANGE: $newText")
                if (!newText.isNullOrEmpty()) {
                    if (viewModel.activeFilterType.value != ActiveFilter.NONE) {
                        Log.d("BoardFragment", "Search Text Change: Chip filters are active. Search will apply to the current subset.")
                    }
                }
                taskAdapter.filter.filter(newText)
                return true
            }
        })

        val closeButton: View? = binding.SearchTitle.findViewById(androidx.appcompat.R.id.search_close_btn)
        closeButton?.setOnClickListener {
            Log.d("BoardFragment", "Search Close Clicked")
            binding.SearchTitle.clearFocus()
            taskAdapter.filter.filter("")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("BoardFragment", "onDestroyView: Setting _binding to null")
        _binding = null
    }
}
