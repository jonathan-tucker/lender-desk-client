package com.carsaver.lender.desk.client.impl

import com.carsaver.lenderdesk.client.swagger.apis.EnhancedIncentivesControllerApi
import com.carsaver.lenderdesk.client.swagger.infrastructure.ApiClient
import com.carsaver.lenderdesk.client.swagger.models.EnhancedIncentivesOfferResponse
import com.carsaver.lenderdesk.client.swagger.models.LenderProgramsRequest
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.commons.codec.digest.DigestUtils
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

val formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd")

class LenderDeskClientImpl(
    private val appId: String,
    private val sharedSecret: String
) : LenderDeskClient {

    private val api: EnhancedIncentivesControllerApi
    private val basePath = "https://lenderdesk.api.chromedata.com/v4.6.premium/lenderDesk"
    private val dealerId = "CARSAVER"
    private val ratesType = "Standard"

    init {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY
        ApiClient.builder.addInterceptor(logger)
        ApiClient.builder.addInterceptor(Interceptor { chain ->
            val request: Request = chain.request()
            val newRequest: Request

            val epochTime = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli()
            println(epochTime)
            val noonce = Random().nextLong()

            val digest = "" + noonce + epochTime + sharedSecret
            val hashed = DigestUtils.sha(digest)
            val encoded = Base64.getEncoder().encode(hashed)

            newRequest = request.newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader(
                    "Authorization", "Atmosphere realm=\"https://carsaver.com\"" +
                            ",chromedata_timestamp=\"${epochTime}\"" +
                            ",chromedata_nonce=\"$noonce\"" +
                            ",chromedata_app_id=\"$appId\"" +
                            ",chromedata_digest_method=\"SHA1\"" +
                            ",chromedata_version=\"1.0\"" +
                            ",chromedata_secret_digest=\"${encoded.toString(Charset.defaultCharset())}\""
                )
                .build()
            chain.proceed(newRequest)
        })

        api = EnhancedIncentivesControllerApi(

        )
    }

    // zip is users or, if no user, dealers zip code
    fun fetchEnhancedIncentivesOffers(
        vin: String,
        zip: String,

        creditScore: Int? = null,
        lenders: List<String>? = null,
        previousOwnershipStyleID: Int? = null,
        ratesType: String? = null,
        terms: List<Int>? = null,
        transType: TransactionType? = null,
        vehicleStatus: List<VehicleStatus>? = null,
    ): EnhancedIncentivesOfferResponse {
        val request = LenderProgramsRequest(
            creditScore = creditScore,
            lenders = lenders,
            previousOwnershipStyleID = previousOwnershipStyleID,
            ratesType = ratesType,
            terms = terms,
            transType = transType?.name,
            vehicleStatus = vehicleStatus?.map { it.name },
            programDate = LocalDate.now(ZoneId.of("America/New_York")).format(formatter)
        )
        return api.getEnhancedIncentivesOffersUsingPOST(
            vehicleId = vin,
            zipCode = zip,
            dealerId = dealerId, request = request
        )
    }
}

enum class TransactionType {
    Lease, Loan, Cash
}

enum class VehicleStatus {
    New, Used, CPOV
}