package com.example.task_5

import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface BankApi {
    @GET("api/atm?city=Гомель")
    fun getListOfBanks(): Single<List<Bank>>

    @GET("api/infobox?city=Гомель")
    fun getListOfInfobox(): Single<List<Bank>>

    @GET("api/filials_info?city=Гомель")
    fun getListOfFilials(): Single<List<Bank>>
}

object BankApiImpl {
    private val retrofit = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://belarusbank.by")
        .build()

    private val BankApiService = retrofit.create(BankApi::class.java)

    suspend fun getListOfBanks(): Single<List<Bank>>? {
        return BankApiService.getListOfBanks()
    }

    suspend fun getListOfInfobox(): Single<List<Bank>> {
        return BankApiService.getListOfInfobox()
    }

    suspend fun getListOfFilials(): Single<List<Bank>> {
        return BankApiService.getListOfFilials()
    }
}