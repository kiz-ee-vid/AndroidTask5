package com.example.task_5

import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import com.example.task_5.Constants.Companion.ATM_URL
import com.example.task_5.Constants.Companion.BASE_URL
import com.example.task_5.Constants.Companion.INFOBOX_URl
import com.example.task_5.Constants.Companion.FILIAL_URL

interface BankApi {
    @GET(ATM_URL)
    fun getListOfAtm(): Single<List<Bank>>

    @GET(INFOBOX_URl)
    fun getListOfInfobox(): Single<List<Bank>>

    @GET(FILIAL_URL)
    fun getListOfFilials(): Single<List<Bank>>
}

object BankApiImpl {
    private val retrofit = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    private val BankApiService = retrofit.create(BankApi::class.java)

    suspend fun getListOfBanks(): Single<List<Bank>>? {
        return BankApiService.getListOfAtm()
    }

    suspend fun getListOfInfobox(): Single<List<Bank>> {
        return BankApiService.getListOfInfobox()
    }

    suspend fun getListOfFilials(): Single<List<Bank>> {
        return BankApiService.getListOfFilials()
    }

}