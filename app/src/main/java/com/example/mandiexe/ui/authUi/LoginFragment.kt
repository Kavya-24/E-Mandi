package com.example.mandiexe.ui.authUi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.mandiexe.R
import com.example.mandiexe.viewmodels.LoginViewModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.PhoneAuthProvider
import com.hbb20.CountryCodePicker

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel
    private lateinit var root: View

    //UI elements
    private lateinit var btn: MaterialButton
    private lateinit var cpp: CountryCodePicker
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

        btn = root.findViewById(R.id.mtb_get_otp)
        cpp = root.findViewById(R.id.countryCodeHolder) as CountryCodePicker


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

    private fun isValidate(): Boolean {

        return cpp.isValidFullNumber
    }

    private fun getOTP() {

        val phNumber = cpp.fullNumberWithPlus
        val bundle = bundleOf(
            "PHONE" to phNumber
        )
        root.findNavController().navigate(R.id.action_nav_login_to_OTPFragment, bundle)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

}
