package com.barengific.posra

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

class EditTextExtension {
    fun EditText.showKeyboard(
    ) {
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    fun EditText.hideKeyboarder(
    ) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }
}