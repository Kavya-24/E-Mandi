package com.example.mandiexe.utils.usables

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar


object UIUtils {

    private val TAG = UIUtils::class.java.simpleName
    fun createSnackbar(value: String?, context: Context, container: View) {
        Snackbar.make(container, value.toString(), Snackbar.LENGTH_SHORT).show()
    }

    fun createToast(value: String, context: Context, container: View) {
        Toast.makeText(context, value, Toast.LENGTH_SHORT).show()
    }

    fun hideKeyboard(activity: Activity, context: Context) {
        val imm: InputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(activity: Activity, context: Context, etInstance: EditText) {

        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager?.showSoftInput(etInstance, InputMethodManager.SHOW_FORCED)
    }

    //Set Up Options from spinner adapter
    fun getSpinnerAdapter(mList: Int, actvInstance: AutoCompleteTextView, context: Context) {

        val array: Array<String> = context.resources.getStringArray(mList)
        val adapter: ArrayAdapter<String>? = context.let {
            ArrayAdapter<String>(
                it, android.R.layout.simple_spinner_item,
                array
            )
        }
        actvInstance.setAdapter(adapter)
        return
    }
}