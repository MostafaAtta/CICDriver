package com.atta.cicdriver.model

data class ChatChannel(val userIds: MutableList<String>) {

    constructor(): this(mutableListOf())
}