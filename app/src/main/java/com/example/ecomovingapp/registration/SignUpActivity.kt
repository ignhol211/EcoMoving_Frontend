package com.example.ecomovingapp.registration

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ecomovingapp.Error
import com.example.ecomovingapp.User
import com.example.ecomovingapp.databinding.SignupActivityBinding
import com.example.ecomovingapp.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpActivity: AppCompatActivity() {

    lateinit var binding: SignupActivityBinding
    private val viewModel : SignUpActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = SignupActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        initObserver()

        binding.bSignUp.setOnClickListener(){
            lifecycleScope.launch(Dispatchers.IO) {
                if(viewModel.validateUserAndPassword(binding.tietEmail.text.toString(),binding.tietPassword.text.toString()))
                {
                viewModel.signUp(User(binding.tietEmail.text.toString(),binding.tietPassword.text.toString()))
                }else{
                    Snackbar.make(binding.root,Error.error2.toString(), Snackbar.LENGTH_LONG).show()
                }
            }

        }

    }
    private fun initObserver() {
        viewModel.user.observe(this){
            if (it){
                Snackbar.make(binding.root,"Usuario registrado correctamente", Snackbar.LENGTH_LONG).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }else{
                Snackbar.make(binding.root,Error.error1.toString(), Snackbar.LENGTH_LONG).show()
            }
        }
    }
}