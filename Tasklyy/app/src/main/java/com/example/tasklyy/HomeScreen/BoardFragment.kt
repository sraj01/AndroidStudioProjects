package com.example.tasklyy.HomeScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasklyy.TaskAdapter
import com.example.tasklyy.TaskViewModel
import com.example.tasklyy.databinding.FragmentBoardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BoardFragment : Fragment() {
    private var _binding : FragmentBoardBinding? = null
    private val binding get() =_binding

    private lateinit var viewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBoardBinding.inflate(inflater, container, false)

         return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.recyclerrview?.layoutManager = LinearLayoutManager(requireContext())
        viewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        viewModel.tasks.observe(viewLifecycleOwner){tasks ->
            adapter = TaskAdapter(tasks)
            binding?.recyclerrview?.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding =null
    }


}