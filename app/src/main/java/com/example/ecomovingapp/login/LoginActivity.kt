package com.example.ecomovingapp.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.activity.viewModels
import com.example.ecomovingapp.Error
import com.example.ecomovingapp.User
import com.example.ecomovingapp.registration.SignUpActivity
import com.example.ecomovingapp.databinding.LoginActivityBinding
import com.example.ecomovingapp.journey.MapsActivity
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityBinding
    private val viewModel : LoginActivityViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = LoginActivityBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initObserver()

        binding.tietEmail.doAfterTextChanged {
            viewModel.validateUserAndPassword(binding.tietEmail.text.toString(),binding.tietPassword.text.toString())
        }
        binding.tietPassword.doAfterTextChanged {
            viewModel.validateUserAndPassword(binding.tietEmail.text.toString(),binding.tietPassword.text.toString())
        }

        binding.login.setOnClickListener {
            viewModel.login(User(binding.tietEmail.toString(),binding.tietPassword.toString()))
        }

        binding.tvSignUp.setOnClickListener {
            openSignUpActivity()
        }
    }

    private fun openSignUpActivity() {
        val intent = Intent(this,SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun initObserver() {
        viewModel.isVisible.observe(this){
            if(it) setVisible() else setGone()
        }

        viewModel.error.observe(this){
            showSnackBar(it)
        }

        viewModel.user.observe(this){
            it?.let {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setVisible(){
        binding.login.visibility = View.VISIBLE
    }
    private fun setGone(){
        binding.login.visibility = View.GONE
    }
    private fun showSnackBar(error: Error) {
        Snackbar.make(binding.root,error.toString(),Snackbar.LENGTH_LONG).show()
    }

}
