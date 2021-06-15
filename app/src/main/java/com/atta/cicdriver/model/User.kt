package com.atta.cicdriver.model

import java.io.Serializable

data class User(var email: String, var firstName: String, var lastName: String ,
                var phone: String, var tokens: MutableList<String>, var type: String,
                var collegeId: String, var enabled: Boolean,var routeId: String,
                var routeName: String) : Serializable{
    var id: String = ""
    constructor(): this("", "", "", "", mutableListOf(), "",
            "", false, "", "")

}
