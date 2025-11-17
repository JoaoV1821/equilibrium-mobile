package com.ufpr.equilibrium.utils

import android.content.Context
import com.ufpr.equilibrium.R

object ErrorMessages {
    fun forHttpStatus(context: Context, code: Int?): String {
        if (code == null) return context.getString(R.string.error_generic)
        return when (code) {
            400, 422 -> context.getString(R.string.error_bad_request)
            401 -> context.getString(R.string.error_unauthorized)
            403 -> context.getString(R.string.error_forbidden)
            404 -> context.getString(R.string.error_not_found)
            409 -> context.getString(R.string.error_conflict)
            429 -> context.getString(R.string.error_too_many_requests)
            in 500..599 -> context.getString(R.string.error_server)
            else -> context.getString(R.string.error_unknown)
        }
    }
}


