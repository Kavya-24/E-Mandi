package com.example.mandiexe.ui.authUi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.mandiexe.R
import com.example.mandiexe.utils.ExternalUtils.createSnackbar
import com.example.mandiexe.viewmodels.LoginViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.login_fragment.*

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel
    private lateinit var root: View

    //UI elements
    private lateinit var btn: MaterialButton

    private val TAG = LoginFragment::class.java.simpleName

    private var verificationInProgress = false
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private lateinit var etNumber: TextInputEditText
    private lateinit var etCode: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.login_fragment, container, false)

        //UI init

        btn = root.findViewById(R.id.buttonContinue)
        etNumber = root.findViewById(R.id.editTextPhone)
        etCode = root.findViewById(R.id.editTextCountryCode)


        btn.setOnClickListener {
            if (isValidate()) {
                getOTP()
            } else {
                createSnackbar(
                    resources.getString(R.string.invalidPhoneError),
                    requireContext(),
                    long_con
                )
            }
        }



        return root
    }

    private fun isValidate(): Boolean {

        return etNumber.text.toString()
            .isNotEmpty() && etNumber.text.toString().length == 10 && etCode.text.toString()
            .isNotEmpty() && validNumber()

    }

    private fun validNumber(): Boolean {

        val check = etNumber.text.toString()

        for (i in check) {
            if (i != '0' && i != '1' && i != '2' && i != '3' && i != '4' && i != '5' && i != '6' && i != '7' && i != '8' && i != '9') {
                return false
            }
        }

        return true
    }

    private fun getOTP() {

        val phNumber = etCode.text.toString() + etNumber.text.toString()
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
