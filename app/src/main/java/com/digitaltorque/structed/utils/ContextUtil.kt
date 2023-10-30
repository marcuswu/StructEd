package com.digitaltorque.structed.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast

tailrec fun Context.getActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}
fun Context.errorMessage(error: String) {
    Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
}
