package com.example.mobileapps1

data class Contact(
    var name: String,
    var phone: String,
    var id: Int = -1,
    var address: Address = Address("NA", "NA", "NA", GpsLocation(0.0f, 0.0f))
)