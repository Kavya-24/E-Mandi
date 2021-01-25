package com.example.mandiexe.ui.authUi

import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.mandiexe.R
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.authBody.LoginBody
import com.example.mandiexe.models.responses.auth.LoginResponse
import com.example.mandiexe.ui.home.MainActivity
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.auth.PreferenceManager
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.auth.SessionManager
import com.example.mandiexe.utils.usables.UIUtils
import com.example.mandiexe.utils.usables.UIUtils.createSnackbar
import com.example.mandiexe.utils.usables.ValidationObject
import com.example.mandiexe.viewmodels.OTViewModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
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
    private val mySupplyService = RetrofitClient.getAuthInstance()

    private lateinit var tvTimer: TextView

    private lateinit var timer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.o_t_fragment, container, false)

        otpTextView = root.findViewById(R.id.otpView)
        pb = root.findViewById(R.id.pb_otp_verify)

        pb.visibility = View.VISIBLE

        //Get phone number from arguments
        phoneNumber = arguments?.getString("PHONE")!!
        Log.e(TAG, phoneNumber)

        root.findViewById<TextView>(R.id.tv_phoneNumber_ans).text = phoneNumber
        tvTimer = root.findViewById<TextView>(R.id.tv_timer_resend)

        getOTP()
        pb.visibility = View.VISIBLE

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
            if (ValidationObject.validateOTP(mOtp)) {
                verifyPhoneNumberWithCode(storedVerificationId, mOtp)
            } else {
                //Invalid OTP
                createSnackbar(
                    resources.getString(R.string.invalidOtp),
                    requireContext(),
                    container_frag_otp
                )
            }

            pb.visibility = View.GONE
        }


        root.findViewById<TextView>(R.id.tv_resend).setOnClickListener {

            Log.e(TAG, "In resent")
            if (root.findViewById<TextView>(R.id.tv_resend).text == resources.getString(R.string.otpResend)) {
                //finsih rhe timer
                timer.onFinish()
                resendVerificationCode(phoneNumber, resendToken)
            }
        }


        return root
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {

        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(context as Activity)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())

    }


    private fun getOTP() {

        Log.e(TAG, "Get otp")

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.

                pb.visibility = View.GONE
                Log.e(TAG, "on ver comp " + credential.toString())

                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                pb.visibility = View.GONE
                Log.e(TAG, "on ver failed " + e)
                if (e is FirebaseAuthInvalidCredentialsException) {
                    UIUtils.createSnackbar(
                        resources.getString(R.string.invalidRequest),
                        requireContext(),
                        container_frag_otp
                    )

                } else if (e is FirebaseTooManyRequestsException) {
                    UIUtils.createSnackbar(
                        resources.getString(R.string.quotaRequest),
                        requireContext(),
                        container_frag_otp
                    )

                } else {

                    val error = e.localizedMessage!!
                    Log.e(TAG, "Lovcal messgae" + error + " normat messgae " + e.message.toString())

                    UIUtils.createSnackbar(
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

                Log.e(TAG, "In code sent")
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                //After code has been sent
                //Remove the progress bar
                pb.visibility = View.GONE
                tvTimer.visibility = View.VISIBLE
                root.findViewById<TextView>(R.id.tv_resend).visibility = View.VISIBLE

                //Cretae snackbar
                context?.let {
                    createSnackbar(
                        resources.getString(R.string.otpSent),
                        it, container_frag_otp
                    )
                }

                //Change the text
                root.findViewById<TextView>(R.id.tv_phoneNumber).setText(R.string.otpSend)


                timer = object : CountDownTimer(300000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        tvTimer.text =
                            (millisUntilFinished / 1000).toString()

                    }

                    override fun onFinish() {
                        tvTimer.visibility = View.GONE
                        root.findViewById<TextView>(R.id.tv_resend).text =
                            resources.getString(R.string.otpResend)
                        root.findViewById<TextView>(R.id.tv_resend)
                            .setTextColor(resources.getColor(R.color.wildColor))


                    }
                }.start()
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


    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {

        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        Log.e(TAG, "In signUpWIth with Auth")
        var str = ""

        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    val user = task.result?.user
                    Log.e(TAG, "Firebase user made user" + user.toString())
                    createSnackbar(
                        resources.getString(R.string.successOTP),
                        requireContext(),
                        container_frag_otp
                    )

                    user?.getIdToken(true)?.addOnCompleteListener { mTask ->
                        if (mTask.isSuccessful) {
                            val idToken: String? = mTask.result.token


                            str = idToken!!

                            sendLoginRespone(str)

                            // Send token to your backend via HTTPS
                            // ...

                        } else {

                            // Handle error -> task.getException();
                            createSnackbar(
                                mTask.exception?.localizedMessage.toString(),
                                requireContext(),
                                container_frag_otp
                            )
                        }
                    }


                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        createSnackbar(
                            resources.getString(R.string.invalidCredentials),
                            requireContext(),
                            container_frag_otp
                        )

                    } else if (task.exception is FirebaseNetworkException) {
                        createSnackbar(
                            resources.getString(R.string.error_please_check_internet),
                            requireContext(),
                            container_frag_otp
                        )
                    } else {

                        createSnackbar(
                            task.exception?.localizedMessage.toString(),
                            requireContext(),
                            container_frag_otp
                        )

                    }
                }
            }


    }

    private fun sendLoginRespone(str: String) {

        Log.e(TAG, "In send login")

        val body = LoginBody(str)
        makeCall(body, str)


    }

    private fun makeCall(body: LoginBody, str: String) {

        viewModel.lgnFunction(body).observe(viewLifecycleOwner, Observer { mResponse ->
            val success = viewModel.successful.value
            if (success != null) {
                if (success) {
                    //Either login or new reg
                    checkResponse(mResponse, str, viewModel.message.value!!)
                } else {
                    //Create a snackbar
                    createSnackbar(viewModel.message.value, requireContext(), container_frag_otp)
                }
            }
        })
    }

    private fun checkResponse(mResponse: LoginResponse, str: String, message: String) {

        Log.e(TAG, "In check response and message is " + mResponse.msg + mResponse.user)
        Log.e(TAG, "Firebase Token " + str)

        if (mResponse.msg == "Phone Number not registered.") {

            val bundle = bundleOf(
                "TOKEN" to str,
                "PHONE" to phoneNumber
            )

            UIUtils.createToast(
                resources.getString(R.string.numberVerifed),
                requireContext(),
                container_frag_otp
            )

            val navController = root.findNavController()
            navController.navigateUp()
            navController.navigate(R.id.action_nav_login_to_nav_signup, bundle)

        } else if (mResponse.msg == "Login successful.") {
            successLogin(mResponse)
        }


    }


    private fun successLogin(response: LoginResponse?) {

        Log.e(TAG, "Success Login and response is " + response.toString())

        response?.user?.accessToken?.let { sessionManager.saveAuth_access_Token(it) }

        response?.user?.refreshToken?.let { preferenceManager.putAuthToken(it) }

        Log.e(TAG, "AT: \n" + sessionManager.fetchAcessToken().toString())
        Log.e(TAG, "PT: \n" + preferenceManager.authToken.toString())

        pref.setNumberFromPreference(phoneNumber)
        pref.name = response?.user?.name


        Toast.makeText(context, resources.getString(R.string.loginSuceed), Toast.LENGTH_SHORT)
            .show()

        //Remove timer
        timer.onFinish()
        timer.cancel()
        val intent = Intent(requireContext(), MainActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
        UIUtils.hideKeyboard(requireActivity(), requireContext())
        startActivity(intent)
        //Finish activity
        activity?.finish()


    }

    override fun onDestroy() {
        super.onDestroy()
        timer.onFinish()
        timer.cancel()
        viewModel.successful.removeObservers(this)
        viewModel.successful.value = null

    }


}
