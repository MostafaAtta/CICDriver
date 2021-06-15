package com.atta.cicdriver.model

import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

data class Route(var driverId: String, var driverName: String, var name: String,
                 var morningTimes: Map<String, String>, var afternoonTimes: Map<String, String>,
                 var driverLocation: GeoPoint, var busCapacity: Int,
                 var reservedChairs: Int): Serializable{
    var id: String = ""
    constructor(): this("", "", "", mapOf(), mapOf(),
            GeoPoint(0.0, 0.0), 0, 0)

}
