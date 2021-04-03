package com.atta.cicdriver

import java.io.Serializable

data class Route(var driverId: String, var driverName: String, var name: String, var arrivalTime: String,
                 var startTime: String): Serializable{
    var id: String = ""
    constructor(): this("", "", "", "", "")

}
