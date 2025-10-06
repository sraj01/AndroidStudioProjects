package com.example.tasklyy.AddTaskScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.tasklyy.Local.DB.UserDao
import com.example.tasklyy.Local.DB.taskData.Task
import com.example.tasklyy.Local.DB.UserEntity
import com.example.tasklyy.Utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ActiveFilter {
    NONE,
    STATUS,
    DUE_DATE,
    ASSIGNEE
}

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val userDao: UserDao
) : ViewModel() {

    private val _activeFilterType = MutableLiveData(ActiveFilter.NONE)
    val activeFilterType: LiveData<ActiveFilter> get() = _activeFilterType.distinctUntilChanged()
    private val _statusFilterArgument = MutableLiveData<String?>(null)
    val statusFilterArgument: LiveData<String?> get() = _statusFilterArgument.distinctUntilChanged()

    private val _selectedAssigneeUserId = MutableLiveData<Int?>(null)
    val selectedAssigneeUserId: LiveData<Int?> get() = _selectedAssigneeUserId.distinctUntilChanged()

    val allUsersForAssigneeFilter: LiveData<List<UserEntity>> = userDao.getAllUsers()

    val tasksToDisplay: LiveData<List<Task>> = _activeFilterType.switchMap { filterType ->
        Log.d("AddTaskViewModel", "tasksToDisplay: ActiveFilterType changed to $filterType")
        when (filterType) {
            ActiveFilter.STATUS -> _statusFilterArgument.switchMap { status ->
                Log.d("AddTaskViewModel", "tasksToDisplay: StatusFilterArgument changed to $status")
                if (status != null) {
                    taskRepository.getTasksByStatus(status)
                } else {

                    taskRepository.getAllTasks()
                }
            }
            ActiveFilter.DUE_DATE -> {
                Log.d("AddTaskViewModel", "tasksToDisplay: Applying DueDate filter.")
                val todayStart = DateUtils.getTodayStartMillis()
                val todayEnd = DateUtils.getTodayEndMillis()
                taskRepository.getTasksDueToday(todayStart, todayEnd)
            }
            ActiveFilter.ASSIGNEE -> _selectedAssigneeUserId.switchMap { userId ->
                Log.d("AddTaskViewModel", "tasksToDisplay: SelectedAssigneeUserId changed to $userId")
                if (userId != null) {
                    taskRepository.getTasksByAssignee(userId)
                } else {
                    taskRepository.getAllTasks()
                }
            }
            ActiveFilter.NONE -> {
                Log.d("AddTaskViewModel", "tasksToDisplay: No active filter (NONE). Getting all tasks.")
                taskRepository.getAllTasks()
            }
        }
    }.distinctUntilChanged()

    fun applyStatusFilter(status: String) {
        Log.d("AddTaskViewModel", "applyStatusFilter: $status")
        _statusFilterArgument.value = status
        _selectedAssigneeUserId.value = null
        if (_activeFilterType.value != ActiveFilter.STATUS) {
            _activeFilterType.value = ActiveFilter.STATUS
        }
    }

    fun clearStatusFilter() {
        Log.d("AddTaskViewModel", "clearStatusFilter called.")
        _statusFilterArgument.value = null
        if (_activeFilterType.value == ActiveFilter.STATUS) {
            _activeFilterType.value = ActiveFilter.NONE
        }
    }

    fun applyDueDateFilter() {
        Log.d("AddTaskViewModel", "applyDueDateFilter")
        _statusFilterArgument.value = null
        _selectedAssigneeUserId.value = null
        if (_activeFilterType.value != ActiveFilter.DUE_DATE) {
            _activeFilterType.value = ActiveFilter.DUE_DATE
        }
    }

    fun clearDueDateFilter() {
        Log.d("AddTaskViewModel", "clearDueDateFilter called.")
        if (_activeFilterType.value == ActiveFilter.DUE_DATE) {
            _activeFilterType.value = ActiveFilter.NONE
        }
    }

    fun applyAssigneeFilter(userId: Int?) {
        Log.d("AddTaskViewModel", "applyAssigneeFilter: UserID $userId")
        _selectedAssigneeUserId.value = userId
        _statusFilterArgument.value = null
        if (_activeFilterType.value != ActiveFilter.ASSIGNEE) {
            _activeFilterType.value = ActiveFilter.ASSIGNEE
        }
    }

    fun clearAssigneeFilter() {
        Log.d("AddTaskViewModel", "clearAssigneeFilter called.")
        _selectedAssigneeUserId.value = null
        if (_activeFilterType.value == ActiveFilter.ASSIGNEE) {
            _activeFilterType.value = ActiveFilter.NONE
        }
    }

    fun clearAllFilters() {
        Log.d("AddTaskViewModel", "clearAllFilters called.")
        _statusFilterArgument.value = null
        _selectedAssigneeUserId.value = null
        if (_activeFilterType.value != ActiveFilter.NONE) {
            _activeFilterType.value = ActiveFilter.NONE
        }
    }


    fun saveTask(task: Task) {
        viewModelScope.launch {
            Log.d("AddTaskViewModel", "Saving task: ${task.title}")
            taskRepository.insertTask(task)
        }
    }
}
