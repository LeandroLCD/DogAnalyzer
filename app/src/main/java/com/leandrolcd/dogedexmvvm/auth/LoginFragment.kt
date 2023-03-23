package com.leandrolcd.dogedexmvvm.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.leandrolcd.dogedexmvvm.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

 interface LoginFragmentActions{
     fun onRegisterClick()
 }
    private lateinit var loginFragmentActions : LoginFragmentActions

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginFragmentActions = try{
            context as LoginFragmentActions
        }catch (e: java.lang.ClassCastException){
            throw java.lang.ClassCastException("$context must implement LoginFragmentActions")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLoginBinding.inflate(layoutInflater)

        binding.loginRegisterButton.setOnClickListener{
            loginFragmentActions.onRegisterClick()
        }
        return binding.root
    }


}