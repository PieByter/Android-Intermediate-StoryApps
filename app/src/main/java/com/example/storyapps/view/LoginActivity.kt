package com.example.storyapps.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.storyapps.data.datastore.UserPreference
import com.example.storyapps.data.repository.Repository
import com.example.storyapps.databinding.ActivityLoginBinding
import com.example.storyapps.viewmodel.login.LoginViewModel
import com.example.storyapps.viewmodel.login.LoginViewModelFactory
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val authDataStore = UserPreference.getInstance(this)
        val repository = Repository(authDataStore)
        viewModel =
            ViewModelProvider(this, LoginViewModelFactory(repository))[LoginViewModel::class.java]

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            lifecycleScope.launch {
                viewModel.login(email, password)
            }
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        viewModel.loginResult.observe(this) { loginResponse ->
            if (loginResponse?.loginResult?.token != null) {
                lifecycleScope.launch {
                    saveTokenToDataStore(loginResponse.loginResult.token)
                }
                navigateToMainActivity()
                showToast("Login successful")
            } else {
                hideProgressBar()
                showToast(loginResponse?.message ?: "Login failed")
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                showProgressBar()
            } else {
                hideProgressBar()
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            showToast(message ?: "Login failed")
        }

        lifecycleScope.launch {
            val token = UserPreference.getInstance(this@LoginActivity).getToken().firstOrNull()
            if (token != null) {
                navigateToMainActivity()
                finish()
            }
        }

        // Property Animation
        playAnimation()
    }

    private suspend fun saveTokenToDataStore(token: String) {
        val authDataStore = UserPreference.getInstance(this)
        authDataStore.saveToken(token)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showProgressBar() {
        binding.loginLoading.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.loginLoading.visibility = View.GONE
    }

    // Property Animation
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val welcome = ObjectAnimator.ofFloat(binding.txtWelcome, View.ALPHA, 1f).setDuration(250)
        val welcomeSupport = ObjectAnimator.ofFloat(binding.welcomeSupport, View.ALPHA, 1f)
            .setDuration(250)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(250)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(250)
        val edEmail =
            ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(250)
        val edPassword =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(250)
        val titleEmail =
            ObjectAnimator.ofFloat(binding.txtEmail, View.ALPHA, 1f).setDuration(250)
        val titlePass =
            ObjectAnimator.ofFloat(binding.txtPassword, View.ALPHA, 1f).setDuration(250)
        val together = AnimatorSet().apply {
            playTogether(login, register)
        }
        AnimatorSet().apply {
            playSequentially(
                welcome,
                welcomeSupport,
                titleEmail,
                edEmail,
                titlePass,
                edPassword,
                together
            )
            start()
        }
    }
}

