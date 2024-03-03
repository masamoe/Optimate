package com.example.optimate.loginAndRegister

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
interface FcmApi {


        @POST("/send")
        suspend fun sendMessage(
            @Body body: SendMessageDTO
        )

        @POST("/broadcast")
        suspend fun broadcast(
            @Body body: SendMessageDTO
        )

}