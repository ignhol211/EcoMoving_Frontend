package com.example.ecomovingapp.Login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.activity.viewModels
import com.example.ecomovingapp.Error
import com.example.ecomovingapp.Registration.SignUpActivity
import com.example.ecomovingapp.databinding.LoginActivityBinding
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityBinding
    private val viewModel : LoginActivityViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = LoginActivityBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initObserver()

        binding.tietUser.doAfterTextChanged {
            viewModel.validateUserAndPassword(binding.tietUser.toString(),binding.tietPassword.toString())
        }
        binding.tietPassword.doAfterTextChanged {
            viewModel.validateUserAndPassword(binding.tietUser.toString(),binding.tietPassword.toString())
        }

        binding.bSignIn.setOnClickListener {
            viewModel.signIn(binding.tietUser.toString(),binding.tietPassword.toString())
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
            it?.let {}
        }
    }

    private fun setVisible(){
        binding.bSignIn.visibility = View.VISIBLE
    }
    private fun setGone(){
        binding.bSignIn.visibility = View.GONE
    }
    private fun showSnackBar(error: Error) {
        Snackbar.make(binding.root,error.toString(),Snackbar.LENGTH_LONG).show()
    }

}
