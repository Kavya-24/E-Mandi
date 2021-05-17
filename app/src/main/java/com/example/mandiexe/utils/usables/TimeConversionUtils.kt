package com.example.mandiexe.utils.usables

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

object TimeConversionUtils {


    private val TAG = TimeConversionUtils::class.java.simpleName

    //For the graph util
    @SuppressLint("SimpleDateFormat")
    fun convertDateTimestampUtil(timestamp: String): Date? {
        val calendar = Calendar.getInstance()
        val timezone = TimeZone.getTimeZone("UTC")
        val timeDestinationZone = calendar.timeZone
        val sourceFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        sourceFormat.timeZone = timezone
        val convertedDate = sourceFormat.parse(timestamp)!!

        val destFormat =
            SimpleDateFormat("dd-MM-yy HH:mm")
        destFormat.timeZone = timeDestinationZone

        val resultDate = destFormat.format(convertedDate)


        return destFormat.parse(resultDate)

    }

    @SuppressLint("SimpleDateFormat")
    fun convertTimeToEpoch(timestamp: String): String {

        val calendar = Calendar.getInstance()
        val timezone = TimeZone.getTimeZone("UTC")
        val timeDestinationZone = calendar.timeZone
        val sourceFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val destFormat =
            SimpleDateFormat("dd-MMM-yyyy")

        sourceFormat.timeZone = timezone
        val convertedDate = sourceFormat.parse(timestamp)!!

        destFormat.timeZone = timeDestinationZone
        return destFormat.format(convertedDate)

    }

    //Get the form from reverse of what is required to send in date objects
    @SuppressLint("SimpleDateFormat")
    fun reverseToReq(timestamp: String): String {

        val calendar = Calendar.getInstance()
        val timezone = TimeZone.getTimeZone("UTC")
        val timeDestinationZone = calendar.timeZone
        val sourceFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val destFormat =
            SimpleDateFormat("dd/MM/yyyy")

        sourceFormat.timeZone = timezone
        val convertedDate = sourceFormat.parse(timestamp)!!

        destFormat.timeZone = timeDestinationZone
        return destFormat.format(convertedDate)

    }

    @SuppressLint("SimpleDateFormat")
    fun convertLastModified(timestamp: String): String {

        val calendar = Calendar.getInstance()
        val timezone = TimeZone.getTimeZone("UTC")
        val timeDestinationZone = calendar.timeZone
        val sourceFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val destFormat =
            SimpleDateFormat("dd-MMM-yyyy HH:mm")

        sourceFormat.timeZone = timezone
        val convertedDate = sourceFormat.parse(timestamp)!!
        destFormat.timeZone = timeDestinationZone
        return destFormat.format(convertedDate)


    }

    @SuppressLint("SimpleDateFormat")
    //Convert the calenadr dates
    fun convertDateToReqForm(value: String): String {

        val calendar = Calendar.getInstance()
        val timezone = TimeZone.getTimeZone("UTC")
        val timeDestinationZone = calendar.timeZone

        val sourceFormat =
            SimpleDateFormat("dd/MM/yyyy")
        val destFormat =
            SimpleDateFormat("MM-dd-yyyy 00:00:00")

        sourceFormat.timeZone = timezone
        val convertedDate = sourceFormat.parse(value)!!
        destFormat.timeZone = timeDestinationZone
        Log.e("Timezone", timezone.toString() + timeDestinationZone.toString())

        return destFormat.format(convertedDate)


    }

    //Date Util
    fun getDateInstanceFromString(mDate: String): Date {
        val myFormat = SimpleDateFormat("dd/MM/yyyy") //In which you need put here
        return myFormat.parse(mDate)!!
    }

    //Get Date Object
    fun getDateObject(
        myCalendar: Calendar,
        etInstance: EditText,
        context: Context
    ): DatePickerDialog.OnDateSetListener {

        val today = Calendar.getInstance()
        val now = today.timeInMillis
        Log.e(TAG, "Now is " + now.toString())

        val dateObject =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                //View is the date picker object

                view.minDate = System.currentTimeMillis() - 1000;
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabelOfDate(etInstance, context, myCalendar)
            }


        return dateObject
    }

    //Get Updated Labels
    fun updateLabelOfDate(etInstance: EditText, context: Context, myCalendar: Calendar) {

        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        etInstance.setText(sdf.format(myCalendar.time))

    }

    //External Util
    fun clickOnDateObject(myCalendar: Calendar, etInstance: EditText, context: Context) {

        val mDateObect = getDateObject(myCalendar, etInstance, context)
        context.let { it1 ->
            //it1 is the datepicker object
            DatePickerDialog(
                it1, mDateObect, myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

    }

    fun validateDates(
        toBeBeforeDate: EditText,
        toBeAfterDate: EditText,
        mErrorBefore: Int,
        mErrorLess: Int,
        etInstanceForError: EditText,
        tvInstanceForError: TextInputLayout,
        context: Context
    ): Boolean {
        val date1 = getDateInstanceFromString(toBeBeforeDate.text.toString())
        val date2 = getDateInstanceFromString(toBeAfterDate.text.toString())

        val diff = date2.time - date1.time
        val duffDayCount = diff / (24 * 60 * 60 * 1000)
        var isValid = true

        if (date2.before(date1)) {
            isValid = false
            tvInstanceForError.error = ExternalUtils.getStringFromResoucrces(context, mErrorBefore)

        } else if (duffDayCount < 20) {
            isValid = false
            tvInstanceForError.error = ExternalUtils.getStringFromResoucrces(context, mErrorLess)
        } else {
            tvInstanceForError.error = null
        }


        return isValid
    }


}