package com.example.ecomovingapp.Registration

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.ecomovingapp.databinding.SignupActivityBinding

class SignUpActivity: AppCompatActivity() {

    lateinit var binding: SignupActivityBinding
    private val viewModel : SignUpActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = SignupActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}