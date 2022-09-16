package com.example.submission1.story

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submission1.*
import com.example.submission1.data.StoriesResult
import com.example.submission1.data.UserPreference
import com.example.submission1.databinding.ActivityStoryBinding
import com.example.submission1.login.LoginActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class StoryActivity : AppCompatActivity() {

    private lateinit var storyViewModel: StoryViewModel
    private lateinit var binding: ActivityStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.storyActivityTitle)

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStory.addItemDecoration(itemDecoration)

        setupViewModel()

    }

    private fun setupViewModel() {
        storyViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), applicationContext, dataStore)
        )[StoryViewModel::class.java]

        storyViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {

                val adapter = StoryPagingAdapter()
                binding.rvStory.adapter = adapter
                adapter.setOnItemClickCallback(object : StoryPagingAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: StoriesResult) {
                        showSelectedUser(data)
                    }
                })

                storyViewModel.story.observe(this, {
                    adapter.submitData(lifecycle, it)
                })

                binding.btnAddStory.setOnClickListener {
                    val intent = Intent(this@StoryActivity, AddStoryActivity::class.java)
                    intent.putExtra(AddStoryActivity.EXTRA_TOKEN, user.token)
                    startActivity(intent)
                }
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }


    private fun showSelectedUser(user: StoriesResult) {
        val intentToDetail = Intent(this, DetailActivity::class.java)
        intentToDetail.putExtra(DetailActivity.EXTRA_PHOTO, user.photo)
        intentToDetail.putExtra(DetailActivity.EXTRA_DESC, user.description)
        intentToDetail.putExtra(DetailActivity.EXTRA_NAME, user.name)
        startActivity(intentToDetail)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                storyViewModel.logout()
                return true
            }
            R.id.language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }
            R.id.maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                return true
            }
            else -> return true
        }
    }
}