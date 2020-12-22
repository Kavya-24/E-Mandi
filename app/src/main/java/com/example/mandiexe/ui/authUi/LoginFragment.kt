package com.example.mandiexe.ui.authUi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.mandiexe.R
import com.example.mandiexe.viewmodels.LoginViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.PhoneAuthProvider

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel
    private lateinit var root: View

    //UI elements
    private lateinit var til: TextInputLayout
    private lateinit var et: EditText
    private lateinit var btn: MaterialButton

    private val TAG = LoginFragment::class.java.simpleName

    private var verificationInProgress = false
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.login_fragment, container, false)

        //UI init
        til = root.findViewById(R.id.tilPhoneNumber)
        et = root.findViewById(R.id.actv_phone_number)
        btn = root.findViewById(R.id.mtb_get_otp)


        root.findViewById<ProgressBar>(R.id.pb_login).visibility = View.GONE
        btn.setOnClickListener {
            if (isValidate()) {
                root.findViewById<ProgressBar>(R.id.pb_login).visibility = View.VISIBLE
                getOTP()
            }
        }


        root.findViewById<ProgressBar>(R.id.pb_login).visibility = View.GONE

        return root
    }

    private fun getOTP() {

        val bundle = bundleOf(
            "PHONE" to et.text.toString()
        )
        root.findNavController().navigate(R.id.action_nav_login_to_OTPFragment, bundle)

    }

    private fun isValidate(): Boolean {
        var isTrue = true

        if (et.text.length < 10) {
            isTrue = false
            til.error = resources.getString(R.string.invalidPhoneError)
        } else {
            til.error = null
        }


        return isTrue
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

}
