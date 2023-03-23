package com.leandrolcd.dogedexmvvm.api

import com.leandrolcd.dogedexmvvm.BASE_URL
import com.leandrolcd.dogedexmvvm.api.models.DogResponse
import com.leandrolcd.dogedexmvvm.api.models.SignUpDTO
import com.leandrolcd.dogedexmvvm.api.models.SignUpResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()


interface ApiService{
    @GET("dogs")
    suspend fun getAllDogs(): DogResponse

    @POST("sign_in")
    suspend fun signUp(@Body signUpDTO: SignUpDTO): SignUpResponse

}

object DogsApi{
 val retrofitService: ApiService by lazy {
     retrofit.create(ApiService::class.java)
 }
}