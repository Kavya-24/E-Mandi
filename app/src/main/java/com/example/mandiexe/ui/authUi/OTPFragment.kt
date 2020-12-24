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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.mandiexe.R
import com.example.mandiexe.interfaces.RetrofitClient
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
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.o_t_fragment.*
import retrofit2.Call
import retrofit2.Response
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
    private var mMessage = ""


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

        root.findViewById<TextView>(R.id.tv_phoneNumber).text =
            resources.getString(R.string.otpSend) + phoneNumber
        tvTimer = root.findViewById<TextView>(R.id.tv_timer_resend)

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

        if (tvTimer.isVisible) {
            tvTimer.setOnClickListener {
                if (tvTimer.text == resources.getString(R.string.otpResend)) {
                    //resend otp
                    resendVerificationCode(phoneNumber, resendToken)
                }
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

        Log.e(TAG, "Get otp")

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                Log.e(TAG, "on ver comp " + credential.toString())
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)
                Log.e(TAG, "on ver failed " + e)
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
                Log.e(TAG, "In code sent")
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                //After code has been sent

                tvTimer.visibility = View.VISIBLE

                object : CountDownTimer(300000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        tvTimer.text =
                            resources.getString(R.string.resendIn) + millisUntilFinished / 1000

                    }

                    override fun onFinish() {
                        tvTimer.text = resources.getString(R.string.otpResend)
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


//        viewModel.lgnFunction(body).observe(viewLifecycleOwner, Observer { mResponse ->
//            if (viewModel.successful.value != null) {
//
//                Log.e(TAG, "In sccess true AND its value is " + viewModel.successful.value)
//                if (viewModel.successful.value!!)
//                    checkResponse(mResponse, str)
//                else
//                    createSnackbar(
//                        resources.getString(R.string.failedLogin),
//                        requireContext(),
//                        container_frag_otp
//                    )
//
//            }
//
//        })


    }

    private fun makeCall(body: LoginBody, str: String) {

        var mResponse = LoginResponse("", LoginResponse.User("", "", true, "", "", ""), "")


        mySupplyService.getLogin(
            mLoginBody = body
        )
            .enqueue(object : retrofit2.Callback<LoginResponse> {


                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

                    Log.e(TAG, "Throwable message " + t.message + " Cause " + t.cause)
                    mMessage = ExternalUtils.returnStateMessageForThrowable(t)

                }

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {

                    Log.e(
                        TAG,
                        " In on response " + response.message() + response.body()?.msg + response.body()
                            .toString() + response.body()?.user.toString()
                    )


                    if (response.isSuccessful) {


                        if (response.body()?.msg == "Login successful.") {
                            mMessage =
                                requireContext().resources.getString(R.string.loginSuceed)


                        } else if (response.body()?.msg == "Phone Number not registered.") {
                            mMessage = requireContext().resources.getString(R.string.loginNew)

                        } else {
                            mMessage = response.body()?.msg.toString()
                        }


                    } else {
                        mMessage = response.body()?.msg.toString()
                    }

                    mResponse = response.body()!!
                }
            })

        checkResponse(mResponse, str, mMessage)
    }

    private fun checkResponse(mResponse: LoginResponse, str: String, message: String) {

        Log.e(TAG, "In check response and message is " + mResponse.msg + mResponse.user)

        if (mResponse.msg == "Phone Number not registered.") {

            //Remove timer
            val bundle = bundleOf(
                "TOKEN" to str,
                "PHONE" to phoneNumber
            )
            root.findNavController().navigate(R.id.action_nav_otp_to_nav_signup, bundle)

        } else if (mResponse.msg == "Login successful.") {
            successLogin(mResponse)
        } else {
            createSnackbar(
                message,
                requireContext(),
                container_frag_otp
            )
        }


    }


    private fun successLogin(response: LoginResponse) {

        Log.e(TAG, "Success Login and response is " + response.toString())
        sessionManager.saveAuth_access_Token(
            LoginResponse(
                response.msg,
                response.user,
                response.error
            ).user.accessToken
        )

        sessionManager.saveAuth_refresh_Token(
            (LoginResponse(
                response.msg,
                response.user,
                response.error
            )).user.refreshToken
        )

        preferenceManager.putAuthToken(
            (LoginResponse(
                response.msg,
                response.user,
                response.error
            )).user.accessToken
        )


        //Set phone
        pref.setNumberFromPreference(phoneNumber)


        Toast.makeText(context, resources.getString(R.string.loginSuceed), Toast.LENGTH_LONG)
            .show()
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
        hideKeyboard(requireActivity(), requireContext())
        startActivity(intent)


    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.successful.removeObservers(this)
        viewModel.successful.value = null

    }


}
