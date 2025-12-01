package com.keylab.mobile.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.keylab.mobile.R

class PaymentBottomSheetFragment(
    private val onPaymentConfirmed: (String) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var etCardNumber: TextInputEditText
    private lateinit var etExpiryDate: TextInputEditText
    private lateinit var etCvc: TextInputEditText
    private lateinit var btnPay: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etCardNumber = view.findViewById(R.id.etCardNumber)
        etExpiryDate = view.findViewById(R.id.etExpiryDate)
        etCvc = view.findViewById(R.id.etCvc)
        btnPay = view.findViewById(R.id.btnPay)

        setupCardFormatter()
        setupExpiryDateFormatter()

        btnPay.setOnClickListener {
            val cardNumber = etCardNumber.text.toString().replace(" ", "")
            val expiry = etExpiryDate.text.toString()
            val cvc = etCvc.text.toString()

            if (validateInputs(cardNumber, expiry, cvc)) {
                onPaymentConfirmed(cardNumber)
                dismiss()
            }
        }
    }

    private fun validateInputs(cardNumber: String, expiry: String, cvc: String): Boolean {
        if (cardNumber.length < 16) {
            etCardNumber.error = "Número de tarjeta inválido"
            return false
        }
        if (expiry.length != 5) {
            etExpiryDate.error = "Formato MM/AA requerido"
            return false
        }
        if (cvc.length < 3) {
            etCvc.error = "CVC inválido"
            return false
        }
        return true
    }

    private fun setupCardFormatter() {
        etCardNumber.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                val str = s.toString().replace(" ", "")
                val formatted = StringBuilder()
                for (i in str.indices) {
                    if (i > 0 && i % 4 == 0) {
                        formatted.append(" ")
                    }
                    formatted.append(str[i])
                }

                etCardNumber.setText(formatted.toString())
                etCardNumber.setSelection(formatted.length)
                isFormatting = false
            }
        })
    }
    
    private fun setupExpiryDateFormatter() {
         etExpiryDate.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                val str = s.toString().replace("/", "")
                val formatted = StringBuilder()
                for (i in str.indices) {
                    if (i == 2) {
                        formatted.append("/")
                    }
                    formatted.append(str[i])
                }

                etExpiryDate.setText(formatted.toString())
                etExpiryDate.setSelection(formatted.length)
                isFormatting = false
            }
        })
    }

    companion object {
        const val TAG = "PaymentBottomSheet"
    }
}
