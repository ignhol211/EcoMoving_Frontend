package com.example.ecomovingapp.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
            viewModel.login(User(binding.tietEmail.text.toString(),binding.tietPassword.text.toString()))
        }

        binding.tvSignUp.setOnClickListener {
            openSignUpActivity()
        }
    }

    private fun initObserver() {
        viewModel.isVisible.observe(this){
            if(it) setVisible() else setGone()
        }

        viewModel.error.observe(this){
            showSnackBar(it)
        }

        viewModel.authUser.observe(this){
            it?.let {
                MapsActivity.launch(this@LoginActivity,it.token.toString())
            }
        }
    }

    private fun setVisible(){
        /**
         * Hide labels
         */
        binding.userFormat.visibility = View.VISIBLE
        binding.passwordFormat.visibility = View.VISIBLE
        binding.login.visibility = View.GONE
    }
    private fun setGone(){
        /**
         * Show labels
         */
        binding.userFormat.visibility = View.GONE
        binding.passwordFormat.visibility = View.GONE
        binding.login.visibility = View.VISIBLE
    }
    private fun showSnackBar(error: Error) {
        /**
         * Show snackbar
         *
         * @param error:Error
         */
        Snackbar.make(binding.root,error.toString(),Snackbar.LENGTH_LONG).show()
    }

    private fun openSignUpActivity() {
        /**
         * Launch SignUpActivity
         */
        val intent = Intent(this,SignUpActivity::class.java)
        startActivity(intent)
    }

}
