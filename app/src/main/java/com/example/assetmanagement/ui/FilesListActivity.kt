package com.example.assetmanagement.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assetmanagement.base.ui.BaseActivity
import com.example.assetmanagement.data.model.ExcellFile
import com.example.assetmanagement.ui.adapter.FilesListAdapter
import com.example.assetmanagement.databinding.ActivityFilesListBinding
import com.example.assetmanagement.utils.Constant.Companion.DOC
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class FilesListActivity : BaseActivity<ActivityFilesListBinding>() {
    private var adapter: FilesListAdapter? = null
    override fun inflateLayout(layoutInflater: LayoutInflater): ActivityFilesListBinding  = ActivityFilesListBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun onResume() {
        super.onResume()
        getFiles()
    }

    private fun init() {

        setupClickListener()

        setUpAdapter()
    }

    private fun setupClickListener() {
        binding.excellReader.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setUpAdapter() {
        adapter = FilesListAdapter(this)
        binding.rv.adapter = adapter
        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.setHasFixedSize(true)

    }

    fun getFiles() {
        val listOfFiles: MutableList<ExcellFile> = arrayListOf()
        showProgress()
        val fileDir = File(this.filesDir, DOC)
        if (fileDir.exists()) {
            val files = fileDir.listFiles()
            files?.forEach {
                listOfFiles.add(
                    ExcellFile(it.name, it.absolutePath)
                )
            }
        }

        listOfFiles.reverse()
        hideProgress()
        adapter?.clear()
        adapter?.setData(listOfFiles)
        checkForNoData()
    }

    private fun showProgress() {
        binding.progressBar.visibility = View.VISIBLE

    }

    private fun hideProgress() {
        binding.progressBar.visibility = View.GONE

    }

    private fun checkForNoData() {
        if ((adapter?.itemCount ?: 0) == 0) {
            binding.llNoDataFound.tvNoDataFound.isVisible
        } else {
            binding.llNoDataFound.tvNoDataFound.isInvisible
        }
    }

}
