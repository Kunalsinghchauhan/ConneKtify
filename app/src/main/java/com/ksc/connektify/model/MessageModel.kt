package com.ksc.connektify.model

data class MessageModel(
    var message: String?="",
    var sender : String?="",
    var timeStamp : Long?=0,
)
