package com.atta.cicdriver.model

import com.google.firebase.Timestamp
import java.util.*

data class Message(val text: String,
                   val time: Timestamp,
                   val senderId: String,
                   val recipientId: String,
                   val senderName: String,
                   val type: String) {

    constructor() : this("", Timestamp(Date(0)), "", "",
        "", "")
}