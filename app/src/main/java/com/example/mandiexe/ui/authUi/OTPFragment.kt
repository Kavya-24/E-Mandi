package com.example.mandiexe.ui.authUi

import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.mandiexe.R
import com.example.mandiexe.viewmodels.OTViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.o_t_fragment.*


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
            Snackbar.make(
                container_frag_otp,
                resources.getString(R.string.invalidOtp),
                Snackbar.LENGTH_LONG
            ).show()
        }



        return isValid
    }

    private fun verifyOtp() {

        //Make a call to find wheather this is correct or not


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OTViewModel::class.java)

    }

}
