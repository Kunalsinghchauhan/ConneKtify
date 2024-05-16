package com.ksc.connektify.model

class UsersModel {
    var uID: String? = ""
    var name: String = ""
    var number: String = ""
    var imageURL: String = ""

    constructor()
    constructor(uID: String, name: String, number: String, imageURL: String) {
        this.uID = uID
        this.name = name
        this.number = number
        this.imageURL = imageURL
    }
}
