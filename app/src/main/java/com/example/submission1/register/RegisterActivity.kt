package com.example.submission1.register

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.submission1.R
import com.example.submission1.ViewModelFactory
import com.example.submission1.customview.PasswordAlert
import com.example.submission1.data.ApiConfig
import com.example.submission1.data.StoryResponse
import com.example.submission1.data.User
import com.example.submission1.data.UserPreference
import com.example.submission1.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RegisterActivity : AppCompatActivity() {

    private lateinit var passwordAlert: PasswordAlert
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()

        passwordAlert = binding.passwordEditText

        binding.registerButton.setOnClickListener {

            val name = binding.namaEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && name.isNotEmpty() && password.isNotEmpty()) {
                val client = ApiConfig().getApiService().registerAccount(name, email, password)
                client.enqueue(object : Callback<StoryResponse> {
                    override fun onResponse(
                        call: Call<StoryResponse>,
                        response: Response<StoryResponse>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null && !responseBody.error) {
                                registerViewModel.saveUser(User(name, email, password, false))
                                Toast.makeText(this@RegisterActivity, getString(R.string.succesRegister), Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        } else {
                            Toast.makeText(this@RegisterActivity, response.message(), Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                        Toast.makeText(this@RegisterActivity, getString(R.string.failedRetrofit), Toast.LENGTH_SHORT).show()
                    }

                })
            } else {
                Toast.makeText(this, getString(R.string.tvRegister), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupViewModel() {
        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), applicationContext, dataStore)
        )[RegisterViewModel::class.java]
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}