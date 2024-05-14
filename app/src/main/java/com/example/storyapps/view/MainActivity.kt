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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.storyapps.R
import com.example.storyapps.adapter.MainAdapter
import com.example.storyapps.data.datastore.UserPreference
import com.example.storyapps.databinding.ActivityMainBinding
import com.example.storyapps.di.Injection
import com.example.storyapps.viewmodel.main.MainViewModel
import com.example.storyapps.viewmodel.main.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MainAdapter
    private lateinit var swipeRefresh: SwipeRefreshLayout
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

        setSupportActionBar(findViewById(R.id.mainToolbar))

        binding.rvStory.layoutManager = LinearLayoutManager(this)
        adapter = MainAdapter()
        binding.rvStory.adapter = adapter

        binding.btnAddStory.setOnClickListener {
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }
        swipeRefresh = binding.swipeRefresh

        swipeRefresh.setOnRefreshListener {
            viewModel.loadStories()
        }

        showProgressBar()

        viewModel.storyList.observe(this) { storyList ->
            adapter.submitList(storyList)
            swipeRefresh.isRefreshing = false
            hideProgressBar()
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            showErrorToast(errorMessage)
            swipeRefresh.isRefreshing = false
            hideProgressBar()
        }

        viewModel.loadStories()
    }

    private fun showErrorToast(message: String) {
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

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadStories()
    }

    private fun showProgressBar() {
        binding.mainLoading.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.mainLoading.visibility = View.GONE
    }
}
