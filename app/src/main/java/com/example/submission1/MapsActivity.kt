package com.example.submission1

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.submission1.data.ApiConfig
import com.example.submission1.data.GetStoriesResponse
import com.example.submission1.data.StoriesResult
import com.example.submission1.data.UserPreference
import com.example.submission1.databinding.ActivityMapsBinding
import com.example.submission1.story.StoryViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var storyViewModel: StoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setupViewModel()
        setMapStyle()
    }

    private fun setupViewModel() {
        storyViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), applicationContext, dataStore)
        )[StoryViewModel::class.java]

        storyViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                val service = ApiConfig().getApiService().getStoriesLoc("Bearer ${user.token}", 1)
                service.enqueue(object : Callback<GetStoriesResponse> {
                    override fun onResponse(
                        call: Call<GetStoriesResponse>,
                        response: Response<GetStoriesResponse>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null && !responseBody.error) {
                                setStoryResponse(responseBody.stories)
                            }
                        }
                    }

                    override fun onFailure(call: Call<GetStoriesResponse>, t: Throwable) {
                        Toast.makeText(
                            this@MapsActivity,
                            getString(R.string.failedRetrofit),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }

    private fun setStoryResponse(items: List<StoriesResult>){
        for (data in items){
            val loc = LatLng(data.lat, data.lon)
                mMap.addMarker(
                    MarkerOptions()
                        .position(loc)
                        .title(data.name)
                        .snippet(data.description)
                )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, getString(R.string.styleFailed))
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, getString(R.string.styleError), exception)
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}