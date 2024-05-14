package com.example.storyapps.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.storyapps.R
import com.example.storyapps.data.datastore.UserPreference
import com.example.storyapps.data.repository.Repository
import com.example.storyapps.databinding.ActivityRegisterBinding
import com.example.storyapps.response.ErrorResponse
import com.example.storyapps.viewmodel.register.RegisterViewModel
import com.example.storyapps.viewmodel.register.RegisterViewModelFactory
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val authDataStore = UserPreference.getInstance(this)
        val repository = Repository(authDataStore)
        viewModel =
            ViewModelProvider(
                this,
                RegisterViewModelFactory(repository)
            )[RegisterViewModel::class.java]

        loadImageWithGlide(getString(R.string.register_image_link))

        binding.btnRegisterAcc.setOnClickListener {
            registerAccount()
        }

        // Property Animation
        playAnimation()
    }

    private fun loadImageWithGlide(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.imgRegister)
    }

    private fun registerAccount() {
        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()

        showProgressBar()

        lifecycleScope.launch {
            try {
                val registerResponse = viewModel.register(name, email, password)
                if (!registerResponse.error) {
                    showSuccessDialog()
                } else {
                    showErrorDialog("Registration failed: ${registerResponse.message}")
                }
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody.message
                showErrorDialog("Registration failed: $errorMessage")
            } catch (e: Exception) {
                showErrorDialog("Registration failed. Please try again later.")
                e.printStackTrace()
            } finally {
                hideProgressBar()
            }
        }
    }

    private fun showProgressBar() {
        binding.registerLoading.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.registerLoading.visibility = View.GONE
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setMessage("Registration successful")
            .setPositiveButton("Continue") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    // Property Animation
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val registerAcc =
            ObjectAnimator.ofFloat(binding.btnRegisterAcc, View.ALPHA, 1f).setDuration(250)
        val edRegName =
            ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(250)
        val edRegEmail =
            ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(250)
        val edRegPass =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(250)
        val regName =
            ObjectAnimator.ofFloat(binding.registerName, View.ALPHA, 1f).setDuration(250)
        val regEmail =
            ObjectAnimator.ofFloat(binding.registerEmail, View.ALPHA, 1f).setDuration(250)
        val regPass =
            ObjectAnimator.ofFloat(binding.registerPassword, View.ALPHA, 1f).setDuration(250)
        AnimatorSet().apply {
            playSequentially(
                regName, edRegName, regEmail, edRegEmail, regPass, edRegPass, registerAcc
            )
            start()
        }
    }
}
