package com.example.assetmanagement.ui

import android.Manifest
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.content.FileProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assetmanagement.R
import com.example.assetmanagement.base.ui.BaseActivity
import com.example.assetmanagement.data.model.ListItems
import com.example.assetmanagement.data.model.SingleRow
import com.example.assetmanagement.databinding.ActivityMainBinding
import com.example.assetmanagement.databinding.LayoutDialogBinding
import com.example.assetmanagement.ui.adapter.ListItemAdapter
import com.example.assetmanagement.ui.viewmodel.ExcellReaderViewModel
import com.example.assetmanagement.utils.Constant
import com.example.assetmanagement.utils.Constant.Companion.PATH_IMPORT_EXCEL
import com.example.assetmanagement.utils.copyFileAndExtract
import com.example.assetmanagement.utils.createXlsx
import com.example.assetmanagement.utils.getExtension
import com.example.assetmanagement.utils.isFileEncrypt
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private var adapter: ListItemAdapter? = null
    private lateinit var viewModel: ExcellReaderViewModel
    private var file: File? = null
    private var fileUri: Uri? = null
    private var url: String? = null
    private val listItem = arrayListOf<ListItems>()
    private lateinit var menu: Menu

    private val documentPickResultContract =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { result ->

            var mimeTypeExtension: String?
            result?.also { uri ->
                mimeTypeExtension = uri.getExtension(this)
                if (mimeTypeExtension != null && mimeTypeExtension?.isNotEmpty() == true) {

                    if (mimeTypeExtension?.equals("xlsx", true) != true
                        && mimeTypeExtension?.equals("xls", true) != true
                    ) {
                        Toast.makeText(
                            this,
                            getString(R.string.err_invalid_file_selected),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                copyFileAndExtract(uri, mimeTypeExtension.orEmpty(), {
                    showProgress()
                }, {
                    hideProgress()
                }, { files, uri ->
                    file = files
                    fileUri = uri
                    checkFileEncrypted()
                })
            }
        }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach { _ ->
                openDocument()
            }
        }

    override fun inflateLayout(layoutInflater: LayoutInflater): ActivityMainBinding =
        ActivityMainBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        getIntentData()
        //getLocalData()
    }

    private fun getLocalData() {
        lifecycleScope.launch {
            viewModel.getLocal(null, null)
        }
    }

    private fun getIntentData() {
        fileUri = intent.data
        fileUri.let {
            val mimeTypeExtension = it?.getExtension(this)
            if (it != null) {
                copyFileAndExtract(it, mimeTypeExtension.orEmpty(), {
                    showProgress()
                }, {
                    hideProgress()
                }, { files, uri ->
                    file = files
                    fileUri = uri
                    checkFileEncrypted()
                })
            }
        }
    }

    private fun init() {
        viewModel = ViewModelProvider(this)[ExcellReaderViewModel::class.java]
        viewModel.fileDir = File(this.filesDir, Constant.DOC)
        if (intent.extras?.containsKey(PATH_IMPORT_EXCEL) == true) {
            val filePath = intent.extras?.getString(PATH_IMPORT_EXCEL).orEmpty()
            showProgress()
            if (filePath.isFileEncrypt()) {
                openDialog(filePath)
            } else {
                viewModel.readExcelFileFromAssets(this@MainActivity, filePath)
            }
        }

        attachObserver()
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLInputFactory",
            "com.fasterxml.aalto.stax.InputFactoryImpl"
        )
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLOutputFactory",
            "com.fasterxml.aalto.stax.OutputFactoryImpl"
        )
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLEventFactory",
            "com.fasterxml.aalto.stax.EventFactoryImpl"
        )

        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        setupClickListener()
        setUpAdapter()
        toggleButtonExport()
    }

    private fun setupClickListener() {
        val fab = binding.selectFile
        fab.setOnClickListener {
            checkForStoragePermission()
        }

        binding.btnExport.apply {
            setOnClickListener {
                openSendEmailDialog()
            }
        }
        binding.btnFiles.setOnClickListener {
            val intent = Intent(this, FilesListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openSendEmailDialog() {
        val dialog = BottomSheetDialog(this)

        val view = layoutInflater.inflate(R.layout.layout_send_email, null)

        val btnSend = view.findViewById<Button>(R.id.btn_send)
        val edtSenderName = view.findViewById<EditText>(R.id.edt_sender_name)
        val edtReceiverEmail = view.findViewById<EditText>(R.id.edt_receiver_email)
        val edtEmailTitle = view.findViewById<EditText>(R.id.edt_title_email)
        val edtEmailBody = view.findViewById<EditText>(R.id.edt_body_email)


        btnSend.setOnClickListener {
            val attachmentFile = this@MainActivity.createXlsx(
                getString(
                    R.string.sample_exported_filename,
                    edtSenderName.text.toString()
                ),
                listItem
            )

            val listReceiverEmail = arrayListOf<String>()
            if (edtReceiverEmail.text.toString().contains(",")) {
                val list = edtReceiverEmail.text.toString().split(",")
                list.map {
                    listReceiverEmail.add(it)
                }
            }
            attachmentFile?.let {

                sendToEmail(
                    listReceiverEmail,
                    edtEmailTitle.text.toString(),
                    edtEmailBody.text.toString(),
                    it
                )


//                sendToEmail(
//                    arrayOf(getString(R.string.sample_receiver_email)),
//                    getString(R.string.sample_receiver_title),
//                    getString(
//                        R.string.sample_receiver_message,
//                        getString(R.string.sample_receiver_email),
//                        getString(R.string.sample_exported_filename)
//                    ),
//                    it
//                )
            }
        }
        dialog.setCancelable(true)

        dialog.setContentView(view)

        dialog.show()
    }

    private fun checkFileEncrypted() {
        fileUri?.apply {
            file?.apply {
                if (viewModel.isEncrypt(this.absolutePath)) {
                    openDialog(this.absolutePath)
                } else {
                    viewModel.readExcelFileFromAssets(
                        this@MainActivity,
                        this.absolutePath
                    )
                }
            }
        }
    }

    private fun setUpAdapter() {
        adapter = ListItemAdapter(arrayListOf(), object : ExcelRowClickListener {
            override fun onRowClick(position: Int) {
                viewModel.setPositionCompleted(position)
                adapter?.notifyItemChanged(position)

            }
        }, object : ExcelRowEditListener {
            override fun onRowEdit(cell: Int, row: Int, singleRow: SingleRow) {
                listItem[row].singleRowList[cell].value = singleRow.value
                viewModel.setUpdateDataCell(cell, row, singleRow.value.toString())
            }
        })
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        this.menu = menu
        val searchMenu = menu.findItem(R.id.search_bar)
        val searchView = searchMenu.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                adapter?.getFilter()?.filter(newText)
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_bar -> {
                true
            }

            R.id.actionFilter -> {
                showSortingFilters()
                true
            }

            R.id.actionScan -> {
                scanQrCode()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun scanQrCode() {
        val scanner = IntentIntegrator(this)
        scanner.initiateScan()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            val result: IntentResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                url = result.contents
                setScanResultToSearch()
                Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_SHORT).show();
            }
        } catch (_: Exception) {
        }

    }

    private fun setScanResultToSearch() {
        val searchItem: MenuItem = menu.findItem(R.id.search_bar)
        val searchView = searchItem.actionView as SearchView?
        searchView?.setQuery(url, true)
    }

    private fun showSortingFilters() {
        val firstRow: MutableList<String> = arrayListOf()
        viewModel.excellFirstRowData.observe(this) {
            for (item in it) {
                firstRow.add(item)
            }
        }
        val builder = AlertDialog.Builder(this)
        builder.setSingleChoiceItems(firstRow.toTypedArray(), -1) { dialog, which ->
            val selected = firstRow[which]
            viewModel.sortBy(selected)
            dialog.dismiss()
        }
        builder.create().show()
    }


    private fun openDocument() {
        documentPickResultContract.launch(
            arrayOf(
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
        )
    }

    private fun attachObserver() {
        viewModel.excelExceptionListData.observe(this) {
            it.apply {
                checkForNoData()
                hideProgress()
                binding.llNoDataFound.tvNoDataFound.text = this.orEmpty()
            }
            toggleButtonExport()
        }
        viewModel.excelDataListLiveData.observe(this) {

            it?.apply {
                listItem.clear()
                listItem.addAll(this)

                adapter?.clear()
                adapter?.setData(this)

                lifecycleScope.launch {
                    //viewModel.saveToLocal(viewModel.filename, listItem)
                }


                checkForNoData()
                hideProgress()
            }
            toggleButtonExport()
        }
    }

    private fun toggleButtonExport() {
        binding.btnExport.visibility = if (listItem.isNotEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun checkForNoData() {
        if ((adapter?.itemCount ?: 0) == 0) {
            binding.llNoDataFound.tvNoDataFound.isVisible
        } else {
            binding.llNoDataFound.tvNoDataFound.isGone
        }
    }


    private fun sendToEmail(
        receiver: ArrayList<String>,
        subject: String,
        body: String,
        attachmentFile: File
    ) {
        try {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_EMAIL, receiver)
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                val uri = FileProvider.getUriForFile(
                    this@MainActivity,
                    applicationContext.packageName + ".provider",
                    attachmentFile
                )

                putExtra(Intent.EXTRA_STREAM, uri)
            }
        } catch (t: Throwable) {
            Toast.makeText(this, "Error :  ${t.toString()}", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkForStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            } else {
                openDocument()
            }
        } else {
            openDocument()
        }
    }


    private fun openDialog(path: String) {
        val mDialogView = LayoutDialogBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.enter_password))
        builder.setMessage(getString(R.string.enter_password_desc))
        builder.setView(mDialogView.root)
        builder.setPositiveButton(getString(R.string.submit)) { dialog, _ ->
            val password = mDialogView.editPassword.text.toString()
            if (password.isEmpty()) {
                hideProgress()
                Toast.makeText(this, getString(R.string.err_password_empty), Toast.LENGTH_SHORT)
                    .show()
            } else {
                val result: Boolean = viewModel.checkPassword(password, path)
                if (!result) {
                    Toast.makeText(
                        this,
                        getString(R.string.err_password_incorrect),
                        Toast.LENGTH_SHORT
                    ).show()
                    openDialog(path)
                    hideProgress()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.err_password_incorrect),
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                    viewModel.readExcelFileFromAssets(this@MainActivity, path, password)
                }
            }
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            hideProgress()
            dialog.dismiss()
        }
        builder.create().show()
    }


    private var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                intent.extras?.let {
                    val downloadedFileId = it.getLong(DownloadManager.EXTRA_DOWNLOAD_ID)
                    val downloadManager =
                        getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val uri: Uri = downloadManager.getUriForDownloadedFile(downloadedFileId)
                    this@MainActivity.copyFileAndExtract(
                        uri,
                        MimeTypeMap.getFileExtensionFromUrl(url),
                        {
                            showProgress()
                        },
                        {
                            hideProgress()
                        }, { files, uri ->

                            file = files
                            fileUri = uri
                            checkFileEncrypted()
                        })
                    showProgress()
                }
            }
        }
    }

    private fun showProgress() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progressBar.visibility = View.GONE
    }


}
