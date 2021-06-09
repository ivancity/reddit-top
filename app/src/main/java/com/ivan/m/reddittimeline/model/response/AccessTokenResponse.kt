package com.ivan.m.reddittimeline.model.response

import com.google.gson.annotations.SerializedName

data class AccessTokenResponse (
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresIn: Long
)