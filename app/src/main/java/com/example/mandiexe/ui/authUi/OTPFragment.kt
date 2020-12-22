package com.example.mandiexe.ui.authUi

import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.mandiexe.R
import com.example.mandiexe.models.body.authBody.LoginBody
import com.example.mandiexe.models.responses.auth.LoginResponse
import com.example.mandiexe.ui.home.MainActivity
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.ExternalUtils
import com.example.mandiexe.utils.ExternalUtils.createSnackbar
import com.example.mandiexe.utils.ExternalUtils.hideKeyboard
import com.example.mandiexe.utils.auth.PreferenceManager
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.auth.SessionManager
import com.example.mandiexe.viewmodels.OTViewModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.o_t_fragment.*
import java.util.concurrent.TimeUnit


class OTPFragment : Fragment() {

    companion object {
        fun newInstance() = OTPFragment()
    }

    private val viewModel: OTViewModel by viewModels()
    private lateinit var root: View

    //Instantiate OTP view and get the OTP
    private lateinit var otpTextView: OtpTextView
    private lateinit var pb: ProgressBar


    private var mOtp: String = ""

    private var auth = FirebaseAuth.getInstance()
    private var verificationInProgress = false
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private val TAG = OTPFragment::class.java.simpleName
    private var phoneNumber: String = ""

    private val pref = PreferenceUtil
    private val preferenceManager: PreferenceManager = PreferenceManager()
    private val sessionManager: SessionManager = SessionManager(ApplicationUtils.getContext())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.o_t_fragment, container, false)

        otpTextView = root.findViewById(R.id.otpView)
        pb = root.findViewById(R.id.pb_otp_verify)

        //Get phone number from arguments
        phoneNumber = arguments?.getString("PHONE")!!
        Log.e(TAG, phoneNumber)
        getOTP()


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
                verifyPhoneNumberWithCode(storedVerificationId, mOtp)
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

    private fun getOTP() {


        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")

                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    ExternalUtils.createSnackbar(
                        resources.getString(R.string.invalidRequest),
                        requireContext(),
                        container_frag_otp
                    )

                } else if (e is FirebaseTooManyRequestsException) {
                    ExternalUtils.createSnackbar(
                        resources.getString(R.string.quotaRequest),
                        requireContext(),
                        container_frag_otp
                    )

                } else {

                    val error = e.localizedMessage!!
                    Log.e(TAG, "Lovcal messgae" + error + " normat messgae " + e.message.toString())

                    ExternalUtils.createSnackbar(
                        resources.getString(R.string.invalidRequest),
                        requireContext(),
                        container_frag_otp
                    )

                }

                // Show a message and update the UI
                // ...
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

            }
        }


        auth = FirebaseAuth.getInstance()
        auth.useAppLanguage()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(context as Activity)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

        root.findViewById<ProgressBar>(R.id.pb_login).visibility = View.GONE


    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {

        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        Log.e(
            TAG,
            "In signin with phone auth and auth.uid " + auth.uid.toString() + " " + auth.currentUser?.uid.toString() + " previ is c user id and now cred"
                    + credential.signInMethod.toString() + credential.smsCode.toString() + credential.provider.toString()
        )

        //Save this fuid in savedSharedPreference
        //We have a credential object
        //Save number in the preference
        pref.setFUIDFromPreference(auth.currentUser!!.uid)
        pref.setNumberFromPreference(auth.currentUser!!.phoneNumber!!)


        val body = auth.currentUser?.uid?.let { LoginBody(it) }
        if (body != null) {
            viewModel.lgnFunction(body).observe(viewLifecycleOwner, Observer { mResponse ->
                //Check with the sucessful of it
                if (viewModel.successful.value == false) {
                    createSnackbar(viewModel.message.value, requireContext(), container_frag_otp)
                } else {
                    manageLoginResponse(mResponse)
                }

            })
        }

    }

    private fun manageLoginResponse(mResponse: LoginResponse?) {
        if (mResponse != null) {
            if (mResponse.msg == "Login successful.") {

                //Set the user details
                successLogin(mResponse)

            } else if (mResponse.msg == "Phone Number not registered.") {
                root.findNavController().navigate(R.id.action_nav_otp_to_nav_signup)

            }
        }
    }

    private fun successLogin(response: LoginResponse) {

        sessionManager.saveAuth_access_Token(
            LoginResponse(
                response.msg,
                response.user
            ).user.accessToken
        )

        sessionManager.saveAuth_refresh_Token(
            (LoginResponse(
                response.msg,
                response.user
            )).user.refreshToken
        )

        preferenceManager.putAuthToken(
            (LoginResponse(
                response.msg,
                response.user
            )).user.accessToken
        )

        //Set data in pref
        //TODO: Set the data for retrieval

        Toast.makeText(context, resources.getString(R.string.loginSuceed), Toast.LENGTH_LONG)
            .show()
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
        hideKeyboard(requireActivity(), requireContext())
        startActivity(intent)


    }


}
