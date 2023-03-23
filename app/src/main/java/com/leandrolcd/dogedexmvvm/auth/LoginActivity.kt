package com.leandrolcd.dogedexmvvm.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.leandrolcd.dogedexmvvm.MainActivity
import com.leandrolcd.dogedexmvvm.R
import com.leandrolcd.dogedexmvvm.databinding.ActivityLoginBinding
import com.leandrolcd.dogedexmvvm.dogslist.UiStatus

class LoginActivity : AppCompatActivity(), LoginFragment.LoginFragmentActions,
    SignUpFragment.SignUpFragmentActions {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.uiState.observe(this) { status ->
            when (status) {
                is UiStatus.Error -> {
                    binding.loadingWheel.visibility = View.GONE
                    showErrorDialog(status.message)
                }
                is UiStatus.Loading -> binding.loadingWheel.visibility = View.VISIBLE
                is UiStatus.Success -> binding.loadingWheel.visibility = View.GONE
            }
        }

        viewModel.user.observe(this){
            user ->
            if(user!= null){
                startMainActivity()
            }
        }

    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onRegisterClick() {

        findNavController(R.id.nav_host_fragment)
            .navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
    }

    override fun onSignUpFieldsValidated(
        email: String,
        password: String,
        passwordConfirmation: String
    ) {
        viewModel.signUp(
            email = email,
            password = password,
            passwordConfirmation = passwordConfirmation
        )
    }

    private fun showErrorDialog(message: String){
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("ok"){
                _, _ -> /** Dismiss Dialog **/
            }
            .create()
            .show()
    }
}