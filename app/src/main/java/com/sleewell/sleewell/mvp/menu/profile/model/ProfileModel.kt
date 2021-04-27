package com.sleewell.sleewell.mvp.menu.profile.model

import android.content.Context
import android.util.Log
import com.sleewell.sleewell.api.sleewell.ApiClient
import com.sleewell.sleewell.api.sleewell.IUserApi
import com.sleewell.sleewell.api.sleewell.model.ProfileInfo
import com.sleewell.sleewell.api.sleewell.model.ResponseSuccess
import com.sleewell.sleewell.mvp.menu.profile.ProfileContract
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileModel(context: Context) : ProfileContract.Model {
    private val TAG = "ProfileModelMVP"
    private var api : IUserApi? = ApiClient.retrofit.create(IUserApi::class.java)
    private val token = "7ee9ba8eb8b57356350e8b0d657d46c6d50bfba0".toRequestBody("text/plain".toMediaTypeOrNull())

    override fun getProfileInformation(onProfileInfoListener: ProfileContract.Model.OnProfileInfoListener) {
        val call : Call<ProfileInfo>? = api?.getProfileInformation(token)

        call?.enqueue(object : Callback<ProfileInfo> {

            override fun onResponse(call: Call<ProfileInfo>, response: Response<ProfileInfo>) {
                val responseRes : ProfileInfo? = response.body()

                if (responseRes == null) {
                    Log.e(TAG, "Body null error")
                    Log.e(TAG, "Code : " + response.code())
                    onProfileInfoListener.onFailure(Throwable("Body null error : " + response.code()))
                } else {
                    onProfileInfoListener.onFinished(responseRes)
                }
            }

            override fun onFailure(call: Call<ProfileInfo>, t: Throwable) {
                // Log error here since request failed
                Log.e(TAG, t.toString())
                onProfileInfoListener.onFailure(t)
            }
        })
    }

    override fun updateProfileInformation(
        username: String, firstName: String, lastName: String,
        onFinishedListener: ProfileContract.Model.OnUpdateProfileInfoListener
    ) {
        val call : Call<ResponseSuccess>? = api?.updateProfileInformation(token,
            username.toRequestBody("text/plain".toMediaTypeOrNull()),
            firstName.toRequestBody("text/plain".toMediaTypeOrNull()),
            lastName.toRequestBody("text/plain".toMediaTypeOrNull()),
        )

        call?.enqueue(object : Callback<ResponseSuccess> {

            override fun onResponse(call: Call<ResponseSuccess>, response: Response<ResponseSuccess>) {
                val responseRes : ResponseSuccess? = response.body()

                if (responseRes == null) {
                    Log.e(TAG, "Body null error")
                    Log.e(TAG, "Code : " + response.code())
                    onFinishedListener.onFailure(Throwable("Body null error : " + response.code()))
                } else {
                    onFinishedListener.onFinished(responseRes)
                }
            }

            override fun onFailure(call: Call<ResponseSuccess>, t: Throwable) {
                // Log error here since request failed
                Log.e(TAG, t.toString())
                onFinishedListener.onFailure(t)
            }
        })
    }
}