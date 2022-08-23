package com.hermanbocharov.weatherforecast.exception

import java.io.IOException

class NoInternetException : IOException() {

    override val message: String
        get() = "No internet exception"
}