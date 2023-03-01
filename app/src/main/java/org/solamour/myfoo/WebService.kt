package org.solamour.myfoo

import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface WebService {
    @POST("outer/area/list")
    suspend fun getAreaList(
        @HeaderMap headerMap: Map<String, String>,
        @Body body: String,
    ): String

    @POST("outer/product/list")
    suspend fun getProductList(
        @HeaderMap headerMap: Map<String, String>,
        @Body body: String,
    ): String

    @POST("cert/issueByCsr")
    suspend fun createCerts(
        @HeaderMap headerMap: Map<String, String>,
        @Body body: String,
    ): String

    @POST("cert/queryStatus")
    suspend fun queryStatus(
        @HeaderMap headerMap: Map<String, String>,
        @Body body: String,
    ): String

    /*
    Using "addConverterFactory(GsonConverterFactory.create())" adds backslashes.

    96 chars
    {"areaUuid":"8401aad11849fe3d7c709e700e8edb3d","productUuid":"e3dcf8dedb2ea3d7d3321a512ea38965"}

    106 chars
    "{\"areaUuid\":\"8401aad11849fe3d7c709e700e8edb3d\",\"productUuid\":\"e3dcf8dedb2ea3d7d3321a512ea38965\"}"
    */
    @POST("cert/getCrl")
    suspend fun getCrl(
        @HeaderMap headerMap: Map<String, String>,
        @Body body: String,
    ): String
//    ): Response
}
