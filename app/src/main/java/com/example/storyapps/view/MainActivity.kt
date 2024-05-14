package com.example.storyapps.view

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapps.R
import com.example.storyapps.adapter.LoadingStateAdapter
import com.example.storyapps.adapter.MainAdapter
import com.example.storyapps.data.datastore.UserPreference
import com.example.storyapps.databinding.ActivityMainBinding
import com.example.storyapps.di.Injection
import com.example.storyapps.viewmodel.main.MainViewModel
import com.example.storyapps.viewmodel.main.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MainAdapter
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(
            Injection.provideRepository(this),
            UserPreference.getInstance(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.mainToolbar)

        binding.rvStory.layoutManager = LinearLayoutManager(this)

        adapter = MainAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        binding.btnAddStory.setOnClickListener {
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }

        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading) {
                showProgressBar()
            } else {
                hideProgressBar()
                val errorState = loadState.refresh as? LoadState.Error
                val emptyState = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
                if (errorState != null) {
                    showToast("Error: ${errorState.error.localizedMessage}")
                } else if (emptyState) {
                    showToast("No stories available")
                } else {
                    showToast("Data loaded successfully")
                }
            }
        }

        viewModel.story.observe(this) { pagingData ->
            if (pagingData != null) {
                adapter.submitData(lifecycle, pagingData)
            }
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            hideProgressBar()
            showToast(errorMessage)
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                viewModel.logout()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
                true
            }

            R.id.action_localize -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }

            R.id.action_map -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showProgressBar() {
        binding.mainLoading.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.mainLoading.visibility = View.GONE
    }
}
