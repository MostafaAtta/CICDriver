package com.atta.cicdriver.model

import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

data class RouteRequest(var routeId: String, var routeName: String, var userId: String,
                        var userName: String, var status: String): Serializable{
    var id: String = ""
    constructor(): this("", "", "", "", "")

}
