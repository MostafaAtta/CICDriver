package com.atta.cicdriver

data class User(var email: String, var firstName: String, var lastName: String ,
                var phone: String){
    var id: String = ""
    constructor(): this("", "", "", "")

}
