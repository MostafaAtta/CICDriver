package com.atta.cicdriver.model

import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

data class Point(var name: String, var location: GeoPoint): Serializable {
    var id: String = ""
    constructor(): this("", GeoPoint(0.0, 0.0))
}
