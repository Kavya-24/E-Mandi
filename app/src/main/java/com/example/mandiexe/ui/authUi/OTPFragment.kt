package com.example.mandiexe.ui.authUi

import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.mandiexe.R
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.viewmodels.OTViewModel
import com.google.android.material.button.MaterialButton


class OTPFragment : Fragment() {

    companion object {
        fun newInstance() = OTPFragment()
    }

    private lateinit var viewModel: OTViewModel
    private lateinit var root: View

    //Instantiate OTP view and get the OTP
    private lateinit var otpTextView: OtpTextView
    private lateinit var pb: ProgressBar


    private var mOtp: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.o_t_fragment, container, false)

        otpTextView = root.findViewById(R.id.otpView)
        pb = root.findViewById(R.id.pb_otp_verify)

        otpTextView.otpListener = object : OTPListener {
            override fun onInteractionListener() {

            }

            override fun onOTPComplete(otp: String) {
                //Verify OTP as in otp string
                mOtp = otp
            }

        }


        root.findViewById<MaterialButton>(R.id.mtb_verify_otp).setOnClickListener {
            pb.visibility = View.VISIBLE
            if (isValidOtp()) {
                verifyOtp()
            }

            pb.visibility = View.GONE
        }




        return root
    }

    private fun isValidOtp(): Boolean {
        var isValid = true
        if (mOtp.length < 6) {
            isValid = false
            Toast.makeText(
                context,
                resources.getString(R.string.invalidOtp),
                Toast.LENGTH_LONG
            ).show()
        }



        return isValid
    }

    private fun verifyOtp() {

        //Make a call to find wheather this is correct or not
        /**
         * Test
         */
        Log.e("OTP", PreferenceUtil.getLanguageFromPreference().toString())
        root.findNavController().navigate(R.id.action_nav_otp_to_nav_signup)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OTViewModel::class.java)

    }

}
