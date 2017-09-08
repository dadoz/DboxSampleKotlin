package com.dropbox.core.examples.android

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ProgressBar
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.FolderMetadata
import com.dropbox.core.v2.files.ListFolderResult
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_files.*
import kotlinx.android.synthetic.main.app_bar.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.text.DateFormat
import java.util.*
import application.library.davidelmn.dboxsdkwrapperlibrary.*


/**
 * Activity that displays the content of a path in dropbox and lets users navigate folders,
 * and upload/download files
 */
class FilesActivity : BaseActivity(), EasyPermissions.PermissionCallbacks { //FilesAdapter.Callback , //, ListFolderTask.Callback, DownloadFileTask.Callback, UploadFileTask.Callback {
    private val perms: Array<String> = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    private val mPath: String by lazy { intent.getStringExtra(EXTRA_PATH) ?: "" }
//    private var mSelectedFile: FileMetadata? = null
    private val dialog: ProgressBar by lazy { ProgressBar(this) }
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    /**
     * on create view
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_files)
        onInitView()
    }

    /**
     * on stop handler
     */
    override fun onStop() {
        super.onStop()
        compositeDisposable.dispose()
    }

    /**
     * on activity result
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICKFILE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // This is the result of a call to launchFilePicker
                uploadFile(data?.data.toString())
            }
        }
    }

    /**
     * on init view
     */
    private fun onInitView() {
        //set actionbar
        setSupportActionBar(app_bar)

        //set fab action
        fab.setOnClickListener { performWithPermissions(UPLOAD_FILE) }

        //set rv
        fileListRecyclerViewId.layoutManager = LinearLayoutManager(this)
        fileListRecyclerViewId.adapter = Dbox.buildAdapter()
//        fileListRecyclerViewId.adapter = FilesAdapter(picassoClient, this)
    }

    fun onFolderClicked(folder: FolderMetadata) {
        startActivity(FilesActivity.getIntent(this@FilesActivity, folder.pathLower))
    }

    fun onFileClicked(file: FileMetadata) {
        performWithPermissions(DOWNLOAD_FILE)
    }

    private fun launchFilePicker() {
        // Launch intent to pick file for upload
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, PICKFILE_REQUEST_CODE)
    }

    fun onDataLoaded(result: ListFolderResult) {
        dialog.visibility = View.GONE
        (fileListRecyclerViewId.adapter as FilesAdapter).setFiles(result.entries)
    }

    fun onDownloadComplete(result: File?) {
        dialog.visibility = View.GONE
        result.let { viewFileInExternalApp(result) }
    }

    /**
     * upload complete
     */
    fun onUploadComplete(result: FileMetadata) {
        dialog.visibility = View.GONE
        //set message
        val message = result.name + " size " + result.size + " modified " +
                DateFormat.getDateTimeInstance().format(result.clientModified)
        Snackbar.make(window.decorView.rootView, message, Snackbar.LENGTH_SHORT)
                .show()
        // Reload the folder
        loadData()
    }

    /**
     * commont error handler
     */
    fun onError(e: Exception) {
        dialog.visibility = View.GONE

        Log.e(TAG, "Failed to list folder.", e)
        Snackbar.make(window.decorView.rootView,
                "An error has occurred",
                Snackbar.LENGTH_SHORT)
                .show()
    }


    private fun viewFileInExternalApp(result: File?) {
        val intent = Intent(Intent.ACTION_VIEW)
        val mime = MimeTypeMap.getSingleton()
        val ext = result!!.name.substring(result.name.indexOf(".") + 1)
        val type = mime.getMimeTypeFromExtension(ext)

        intent.setDataAndType(Uri.fromFile(result), type)

        // Check for a handler first to avoid a crash
        val manager = packageManager
        val resolveInfo = manager.queryIntentActivities(intent, 0)
        if (resolveInfo.size > 0) {
            startActivity(intent)
        }
    }

    /**
     * on request permission result
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults:  IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun performWithPermissions(permission: Int) {
        if (EasyPermissions.hasPermissions(this, perms[0])) {
            onPermissionsGranted(permission, Arrays.asList(perms[0]))
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "permission",
                    permission, perms[0])
        }
    }

    override fun onPermissionsGranted(requestCode: Int, list: List<String> ) {
        when (list[0]) {
            Manifest.permission.READ_EXTERNAL_STORAGE -> launchFilePicker()
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> downloadFile()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, list: List<String> ) {
        // Some permissions have been denied
        // ...
    }
    /**
     * PERMISSSION please refactor 100000000 methods to handle 1 permission -,-
     */

//    override fun onRequestPermissionsResult(actionCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        val action = FileAction.fromCode(actionCode)
//
//        var granted = true
//        for (i in grantResults.indices) {
//            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
//                Log.w(TAG, "User denied " + permissions[i] +
//                        " permission to perform file action: " + action)
//                granted = false
//                break
//            }
//        }
//
//        if (granted) {
//            performAction(action)
//        } else {
//            val message = when (action) {
//                FilesActivity.FileAction.UPLOAD -> "Can't upload file: read access denied. " + "Please grant storage permissions to use this functionality."
//                FilesActivity.FileAction.DOWNLOAD -> "Can't download file: write access denied. " + "Please grant storage permissions to use this functionality."
//            }
//
//            Snackbar.make(window.decorView.rootView, message, Toast.LENGTH_LONG).show()
//        }
//    }
//
//    private fun performWithPermissions(action: FileAction) {
//        if (hasPermissionsForAction(action)) {
//            performAction(action)
//            return
//        }
//
//        if (shouldDisplayRationaleForAction(action)) {
//            AlertDialog.Builder(this)
//                    .setMessage("This app requires storage access to download and upload files.")
//                    .setPositiveButton("OK") { dialog, which -> requestPermissionsForAction(action) }
//                    .setNegativeButton("Cancel", null)
//                    .create()
//                    .show()
//        } else {
//            requestPermissionsForAction(action)
//        }
//    }
//
//    private fun hasPermissionsForAction(action: FileAction): Boolean {
//        for (permission in action.permissions) {
//            val result = ContextCompat.checkSelfPermission(this, permission)
//            if (result == PackageManager.PERMISSION_DENIED) {
//                return false
//            }
//        }
//        return true
//    }
//
//    private fun shouldDisplayRationaleForAction(action: FileAction): Boolean {
//        for (permission in action.permissions) {
//            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
//                return true
//            }
//        }
//        return false
//    }
//
//    private fun requestPermissionsForAction(action: FileAction) {
//        ActivityCompat.requestPermissions(
//                this,
//                action.permissions,
//                action.code
//        )
//    }

//    private enum class FileAction constructor(vararg permissions: String) {
//        DOWNLOAD(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//        UPLOAD(Manifest.permission.READ_EXTERNAL_STORAGE);

//        val permissions: Array<out String> = permissions as Array<String>
//
//        val code: Int
//            get() = ordinal
//
//        companion object {
//
//            private val values = values()
//
//            fun fromCode(code: Int): FileAction {
//                if (code < 0 || code >= values.size) {
//                    throw IllegalArgumentException("Invalid FileAction code: " + code)
//                }
//                return values[code]
//            }
//        }
//    }

    companion object {
        @JvmStatic val DOWNLOAD_FILE: Int = 998
        @JvmStatic val UPLOAD_FILE: Int = 999

        private val TAG = FilesActivity::class.java.name

        val EXTRA_PATH = "FilesActivity_Path"
        private val PICKFILE_REQUEST_CODE = 1

        fun getIntent(context: Context, path: String): Intent {
            val filesIntent = Intent(context, FilesActivity::class.java)
            filesIntent.putExtra(FilesActivity.EXTRA_PATH, path)
            return filesIntent
        }
    }
}

