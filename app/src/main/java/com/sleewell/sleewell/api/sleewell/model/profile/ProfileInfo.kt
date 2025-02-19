package com.sleewell.sleewell.api.sleewell.model.profile

import com.google.gson.annotations.SerializedName

data class ProfileInfo(
    @SerializedName("Success")
    val success: String,
    @SerializedName("login")
    val username: String,
    @SerializedName("firstname")
    val firstname: String?,
    @SerializedName("lastname")
    val lastname: String?,
    @SerializedName("email")
    val email: String?
)
