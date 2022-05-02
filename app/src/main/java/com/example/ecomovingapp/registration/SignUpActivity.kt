package com.example.ecomovingapp.registration

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.ecomovingapp.User
import com.example.ecomovingapp.databinding.SignupActivityBinding
import com.google.android.material.snackbar.Snackbar

class SignUpActivity: AppCompatActivity() {

    lateinit var binding: SignupActivityBinding
    private val viewModel : SignUpActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = SignupActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObserver()

        binding.bSignUp.setOnClickListener(){
            viewModel.signUp(User(binding.tietUser.text.toString(),binding.tietPassword.text.toString()))
        }

    }

    private fun initObserver() {
        viewModel.user.observe(this){user->
            Snackbar.make(binding.root,user.toString(), Snackbar.LENGTH_LONG).show()
        }
    }

}