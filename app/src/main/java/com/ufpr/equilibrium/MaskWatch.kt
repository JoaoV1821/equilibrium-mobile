package com.ufpr.equilibrium

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class MaskWatcher(private val editText: EditText, private val mask: String) : TextWatcher {
    private var isUpdating = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        if (s == null || isUpdating) return

        val digitsOnly = s.toString().filter { it.isDigit() }
        var maskedText = ""
        var digitIndex = 0

        for (m in mask) {
            if (m == '#') {
                if (digitIndex < digitsOnly.length) {
                    maskedText += digitsOnly[digitIndex]
                    digitIndex++

                } else {
                    break
                }

            } else {

                if (digitIndex < digitsOnly.length) {
                    maskedText += m
                }
            }
        }

        isUpdating = true
        editText.setText(maskedText)

        editText.setSelection(maskedText.length)
        isUpdating = false
    }

    override fun afterTextChanged(s: Editable?) {

    }
}
