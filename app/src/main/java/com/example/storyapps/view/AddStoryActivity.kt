@file:OptIn(DelicateCoroutinesApi::class)

package com.example.storyapps.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.storyapps.api.ApiService
import com.example.storyapps.data.datastore.UserPreference
import com.example.storyapps.data.repository.StoryRepository
import com.example.storyapps.databinding.ActivityAddStoryBinding
import com.example.storyapps.di.Injection
import com.example.storyapps.helper.getImageUri
import com.example.storyapps.helper.reduceFileImage
import com.example.storyapps.helper.uriToFile
import com.example.storyapps.helper.LocationHelper
import com.example.storyapps.database.StoryDatabase
import com.example.storyapps.viewmodel.addstory.AddStoryViewModel
import com.example.storyapps.viewmodel.addstory.AddStoryViewModelFactory
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var apiService: ApiService
    private lateinit var userPreference: UserPreference
    private lateinit var storyDatabase: StoryDatabase
    private var currentImageUri: Uri? = null
    private var includeLocation: Boolean = false

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        val storyRepository = Injection.provideRepository(this)
        apiService = storyRepository.getApiService()
        userPreference = UserPreference.getInstance(applicationContext)
        storyDatabase = StoryDatabase.getDatabase(this)

        val addStoryRepository = StoryRepository.getInstance(apiService, storyDatabase)
        val addStoryViewModelFactory = AddStoryViewModelFactory(addStoryRepository)
        addStoryViewModel =
            ViewModelProvider(this, addStoryViewModelFactory)[AddStoryViewModel::class.java]

        binding.buttonCamera.setOnClickListener {
            startCamera()
        }

        binding.buttonGallery.setOnClickListener {
            checkPermissionAndStartGallery()
        }

        binding.buttonAdd.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                uploadData()
            }
        }

        binding.includeLocationCheckBox.setOnCheckedChangeListener { _, isChecked ->
            includeLocation = isChecked
        }

    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                showToast("Permission request granted")
                startGallery()
            } else {
                showToast("Permission denied to read your External storage")
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            showToast("No media selected")
        }
    }

    private fun checkPermissionAndStartGallery() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            startGallery()
        }
    }

    private fun startGallery() {
        launcherGallery.launch("image/*")
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    binding.ivItemShowImage.setImageBitmap(imageBitmap)
                }

                REQUEST_IMAGE_PICK -> {
                    data?.data?.let { uri ->
                        currentImageUri = uri
                        showImage()
                    }
                }
            }
        }
    }

    private suspend fun uploadData() {
        showProgressBar()
        val description = binding.edAddDescription.text.toString()
        val imageDrawable = binding.ivItemShowImage.drawable

        if (description.isEmpty() || imageDrawable == null || imageDrawable !is BitmapDrawable) {
            showToast("File not complete. Please complete.")
            hideProgressBar()
            return
        }

        val file = currentImageUri?.let { uriToFile(it, this@AddStoryActivity) }
        if (file != null) {
            try {
                val reducedFile = file.reduceFileImage()
                if (includeLocation) {
                    val locationHelper = LocationHelper(this@AddStoryActivity)
                    val location = locationHelper.getCurrentLocation()
                    addStoryViewModel.uploadImage(reducedFile, description, location)
                } else {
                    // Kirim null jika checkbox tidak tercentang
                    addStoryViewModel.uploadImage(reducedFile, description, null)
                }
                moveToMainActivity()
                showToast("Data successfully uploaded")
            } catch (e: Exception) {
                showToast("Failed to upload data")
                setResult(RESULT_CANCELED)
            }
        } else {
            showToast("Failed to get image file")
        }
        hideProgressBar()
    }

    private fun moveToMainActivity() {
        val intent = Intent(this@AddStoryActivity, MainActivity::class.java).apply {
            flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this@AddStoryActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun showImage() {
        currentImageUri?.let { uri ->
            try {
                val inputStream = contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.ivItemShowImage.setImageBitmap(bitmap)
                } else {
                    showToast("Failed to load image")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Failed to load image")
            }
        }
    }

    private fun showProgressBar() {
        binding.addStoryLoading.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.addStoryLoading.visibility = View.GONE
    }

}
