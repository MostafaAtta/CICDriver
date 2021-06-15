package com.atta.cicdriver.model

import com.google.firebase.Timestamp
import java.util.*

data class MessageSample(val message: String, val messageAr: String,
                         val order: Int) {

    constructor() : this("", "", 0)
}