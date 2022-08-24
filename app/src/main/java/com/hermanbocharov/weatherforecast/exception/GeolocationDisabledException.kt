package com.hermanbocharov.weatherforecast.exception

import java.io.IOException

class GeolocationDisabledException : IOException() {

    override val message: String
        get() = "Location is turned off"
}