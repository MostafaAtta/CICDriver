package com.atta.cicdriver.model

data class UserRoute(var driverId: String, var driverName: String, var routeId: String,
                     var routeName: String, var userId: String, var userName: String){
    var id: String = ""
    constructor(): this("", "", "", "", "", "")

}
