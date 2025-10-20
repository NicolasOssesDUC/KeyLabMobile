package com.keylab.mobile.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.keylab.mobile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: reemplazar con navegación inicial una vez definidos los wireframes.
        binding.mainTitle.text = getString(R.string.placeholder_home_title)
    }
}
