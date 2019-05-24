package com.example.todolist.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

const val BASE_URL = "https://beta.todoist.com/API/v8/"

const val TOKEN = "0d4aeea463922d145cf83e6bac6919e687e79ca5"

private val okHttpClient by lazy {

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient.Builder()
                .addNetworkInterceptor(httpLoggingInterceptor)
                .addInterceptor { chain ->
                        val newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer $TOKEN")
                                .build()
                        chain.proceed(newRequest)
                }
                .build()
}

private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

private var retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

interface TodoistApiService {
        @GET("tasks")
        @Headers("Content-Type: application/json")
        fun getProperties(): Deferred<List<TaskProperty>>

        @POST("tasks")
        @Headers("Content-Type: application/json")
        fun addTask(@Body task: TaskProperty): Deferred<TaskProperty>

        @POST("tasks/{id}/close")
        fun closeTask(@Path("id") id: String): Deferred<ResponseBody>

        @POST("tasks/{id}/reopen")
        fun reOpenTask(@Path("id") id: String): Deferred<ResponseBody>

        @DELETE("tasks/{id}")
        fun deleteTasks(@Path("id")id: String) : Deferred<Response<ResponseBody>>

}

object TaskApi {
        val retrofitService : TodoistApiService by lazy {
                retrofit.create(TodoistApiService::class.java)
        }
}
