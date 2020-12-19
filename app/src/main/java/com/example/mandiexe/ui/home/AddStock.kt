package com.example.mandiexe.ui.home

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.mandiexe.R
import com.example.mandiexe.viewmodels.AddStockViewModel
import java.text.SimpleDateFormat
import java.util.*


class AddStock : Fragment() {


    //Primary constructor
    companion object {
        fun newInstance() = AddStock()
    }

    private lateinit var viewModel: AddStockViewModel
    private lateinit var root: View
    private val myCalendar = Calendar.getInstance()
    private val TAG = AddStock::class.java.simpleName

    //UI variables
    private lateinit var etEst: EditText
    private lateinit var etExp: EditText
    private lateinit var ivLocation: ImageView
    private lateinit var etAddress: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.add_stock_fragment, container, false)


        //UI Init
        etEst = root.findViewById(R.id.etEstDate)
        etExp = root.findViewById(R.id.etExpDate)
        ivLocation = root.findViewById(R.id.iv_location)
        etAddress = root.findViewById(R.id.actv_address)


        //The address will either be preset or will come as an argument from Map Activity
        if (arguments != null) {
            //Set the address in the box trimmed
            etAddress.setText(requireArguments().getString("fetchedLocation").toString())

            Log.e(TAG, "Argument str is" + etAddress.text.toString())
        }

        //Date Instance
        val dateEst =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                val now = myCalendar.timeInMillis
                view.minDate = now

                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabelOfDate()
            }

        val dateExp =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->


                val now = myCalendar.timeInMillis
                view.minDate = now
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabelOfExpiry()
            }


        //##Requires N
        etEst.setOnClickListener {
            context?.let { it1 ->
                DatePickerDialog(
                    it1, dateEst, myCalendar[Calendar.YEAR],
                    myCalendar[Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]
                ).show()
            }
        }

        //##Requires N
        etExp.setOnClickListener {
            context?.let { it1 ->
                DatePickerDialog(
                    it1, dateExp, myCalendar[Calendar.YEAR],
                    myCalendar[Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]
                ).show()
            }
        }

        ivLocation.setOnClickListener {
            //Start an activity
            root.findNavController().navigate(R.id.action_addStock_to_mapActivity)

        }




        return root

    }


    private fun updateLabelOfDate() {

        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        etEst.setText(sdf.format(myCalendar.time))

    }

    private fun updateLabelOfExpiry() {

        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        etExp.setText(sdf.format(myCalendar.time))

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddStockViewModel::class.java)

    }


}
