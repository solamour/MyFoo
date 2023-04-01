package org.solamour.myfoo

import android.bbyh.misccrypto.MisccryptoManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.security.Signature
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.floor

class MyViewModel(
    private val webService: WebService
) : ViewModel() {
    private val miscCryptoManager = MisccryptoManager(null)
    private val csr: String
        get() = "-----BEGIN CERTIFICATE REQUEST-----\n" +
                String(miscCryptoManager.deviceCsr ?: "no csr\n".toByteArray()) +
                "-----END CERTIFICATE REQUEST-----"
    val log = MutableStateFlow(mutableListOf<String>())

    companion object {
        fun factory(webService: WebService) = viewModelFactory {
            initializer {
                MyViewModel(webService)
            }
        }
        private val TAG = MyViewModel::class.qualifiedName

//        const val url = "https://tpmpki-test.tclking.com/pspa/"
//        private const val appKey = "2a8b352d4f76c3156e7d5684e9008a10"
//        private const val appSecret = "03edde71c57b7edaeba240db309595c2"
//        private const val productUuid = "e3dcf8dedb2ea3d7d3321a512ea38965"
        const val url = "https://tpmpki.tclking.com/pspa/"
        private const val appKey = "29ff20bcbc6a86ac45ca2318738734c6"
        private const val appSecret = "28d50e3ebf66ef0c5baedf9cdb47d36c"
        private const val productUuid = "26fe55b495fe45cef5ff964bf930c6b8"

        private const val areaUuid = "8401aad11849fe3d7c709e700e8edb3d"     // "US".
        private const val algorithm = "HmacSHA256"
        private const val imei = "016332000013234"
    }

    @Serializable
    data class Response<T>(
        val code: Int,
        val data: T,
        val msg: String,
    )

    //----------------------------------------------------------------------------------------------
    fun getAreaList() {
        @Serializable
        data class Body(
            val name: String,
        )
        val body = Json.encodeToString(Body(""))
        val headerMap = getHeaderMap(body)

        viewModelScope.launch {
            val response = webService.getAreaList(headerMap, body)

            @Serializable
            data class Data(
                val name: String,
                val uuid: String,
            )
            val responseObj = Json.decodeFromString<Response<List<Data>>>(response)
            log("$responseObj")
        }
    }

    //----------------------------------------------------------------------------------------------
    fun getProductList() {
        @Serializable
        data class Body(
            val name: String,
        )
        val body = Json.encodeToString(Body(""))
        val headerMap = getHeaderMap(body)

        viewModelScope.launch {
            val response = webService.getProductList(headerMap, body)

            @Serializable
            data class Data(
                val name: String,
                val uuid: String,
            )
            val responseObj = Json.decodeFromString<Response<List<Data>>>(response)
            log("${responseObj}\n")
        }
    }

    //----------------------------------------------------------------------------------------------
    @Serializable
    data class CreateCertificatesData(
        val cert: String,
        val certChain: String,
        val signed: String,
    )

    suspend fun createCertificates(): Response<CreateCertificatesData> {
        @Serializable
        data class Body(
            val areaUuid: String,
            val csr: String,
            val termNum: String,
            val productUuid: String,
        )
        val body = Json.encodeToString(
            Body(
                areaUuid,
                csr,
                imei,
                productUuid,
            )
        )
        val headerMap = getHeaderMap(body)

        val response = webService.createCerts(headerMap, body)
        val responseObj = Json.decodeFromString<Response<CreateCertificatesData>>(response)
        log("${responseObj}\n")

        return responseObj
    }

    //----------------------------------------------------------------------------------------------
    // Query certificate's status.
    @Serializable
    data class QueryStatusData(
        val nonce: String,
        val signed: String,
        val status: String,
    )

    suspend fun queryStatus(): Response<QueryStatusData> {
        @Serializable
        data class Body(
            val termNum: String,
            val productUuid: String,
        )
        val body = Json.encodeToString(
            Body(
                imei,
                productUuid,
            )
        )
        val headerMap = getHeaderMap(body)

        val response = webService.queryStatus(headerMap, body)
        val responseObj = Json.decodeFromString<Response<QueryStatusData>>(response)
        log("${responseObj}\n")

        return responseObj
    }

    //----------------------------------------------------------------------------------------------
    fun signWithDeviceKey() {
//        val message = LoremIpsum.getInstance().getParagraphs(1, 1)
        val message = "Hello World"
        log("message: $message")

        val signature = miscCryptoManager.getSignWithDeviceKey(message.toByteArray())
        val result = signature.joinToString(", ") { "0x%02x".format(it) }
        log("signature: $result")

        val isValid = miscCryptoManager.verifySignature(message.toByteArray(), signature)
        log("isValid: $isValid")
    }

    //----------------------------------------------------------------------------------------------
    fun readDeviceCertificate() {
        val deviceCertificate = String(miscCryptoManager.deviceCertificate)
        log("device certificate:\n$deviceCertificate")
    }

    //----------------------------------------------------------------------------------------------
    fun readCertificateChain() {
        val certificateChain = String(miscCryptoManager.certificateChain)
        log("certificate chain:\n$certificateChain")
    }

    //----------------------------------------------------------------------------------------------
    fun writeCertificates() {
        viewModelScope.launch {
            val queryStatus = queryStatus()
            if (queryStatus.data.status != "1") {
                log("query status failed")
                return@launch
            }

            val certificates = createCertificates()
            miscCryptoManager.putDeviceCertificate(certificates.data.cert.toByteArray())
            miscCryptoManager.putCertificateChain(certificates.data.certChain.toByteArray())
        }
    }

    //----------------------------------------------------------------------------------------------
    // Get Certificate Revocation List.
    fun getCrl() {
        @Serializable
        data class Body(
            val areaUuid: String,
            val productUuid: String,
        )
        val body = Json.encodeToString(
            Body(
                areaUuid,
                productUuid,
            )
        )
        val headerMap = getHeaderMap(body)

        viewModelScope.launch {
            val response = webService.getCrl(headerMap, body)

            @Serializable
            data class Data(
                val cert: String,
                val crl: String,
            )
            val responseObj = Json.decodeFromString<Response<Data>>(response)
            log("${responseObj}\n")
        }
    }

    //----------------------------------------------------------------------------------------------
    fun verifySignature() {
        val string = "Hello World"
        val deviceCert = """
           -----BEGIN CERTIFICATE-----
           MIICezCCAiGgAwIBAgIIIwMUBDJYEEQwCgYIKoZIzj0EAwIwfTELMAkGA1UEBgwC
           Q04xEjAQBgNVBAgMCUd1YW5nRG9uZzERMA8GA1UEBwwIU2hlblpoZW4xDDAKBgNV
           BAoMA1RDTDEYMBYGA1UECwwPQkJZSCBERVZJQ0UgTUZSMR8wHQYDVQQDDBZCQllI
           IFRDTCBERVZJQ0UgTUZSIENBMB4XDTIzMDMxMzIwMzI1OFoXDTM3MTAxMTAxNTUx
           NlowNzE1MDMGA1UEAwwsZ1hMZisxNVNEQjdtR0hDWVMzQ05LOXJBVVViQisxSkVG
           aGkzV0tnTkIwbz0wWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAAQefAtKFhF04TQW
           j1WqAJhrOp8JSJuNABoAb9+On8gQKwvtYITt0m3Whh83qHFDEwPPOOKvMELaoI9+
           M2C8av5oo4HQMIHNMD8GA1UdHwQ4MDYwNKAyoDCGLmh0dHA6Ly9jcmwuYmJ5aGVh
           bHRoLmNvbS90Y2xfZGV2aWNlX21mcl9jYS5jcmwwSgYIKwYBBQUHAQEEPjA8MDoG
           CCsGAQUFBzAChi5odHRwOi8vY3JsLmJieWhlYWx0aC5jb20vdGNsX2RldmljZV9t
           ZnJfY2EuY3J0MB0GA1UdDgQWBBR7HCUZMPncyCnnFQoma4Imh+dKMDAfBgNVHSME
           GDAWgBRUJUL7sYbr9U7Ot5SDNjy2dJr86TAKBggqhkjOPQQDAgNIADBFAiEAxXyl
           fpU7dcDLQy30kCB189jdqKIGl1FB19pzogmzOc0CIDQIe/nDn/8M2ezgLlWuENqu
           gXc0DJ658ey27bLbUVkB
           -----END CERTIFICATE-----
           """.trimIndent()
        val signature = byteArrayOf(
            48, 70, 2, 33, 0, -19, 106, -29, 67, -93, -112, 11, -111, 120, -46, -62, 117, -118, -66,
            -95, -100, -2, -122, 28, 91, 32, -94, 56, 107, -54, 100, 2, -106, 16, 97, 25, -123, 2,
            33, 0, -122, -71, 0, -6, 124, 97, 80, 121, -81, -55, -128, 1, 78, 17, 4, -20, -9, 109,
            84, 97, 88, 2, 108, -14, -33, 103, 85, 81, 103, -108, 18, -58,
        )

        val certificate: X509Certificate
        ByteArrayInputStream(deviceCert.toByteArray()).use { inputStream ->
            certificate = CertificateFactory.getInstance("X.509").generateCertificate(inputStream)
                    as X509Certificate
        }
        val isValid = Signature.getInstance("SHA256withECDSA").apply {
            initVerify(certificate)
            update(string.toByteArray())
        }.verify(signature)
        println("isValid: $isValid\n")
    }

    //----------------------------------------------------------------------------------------------
    private fun getHeaderMap(body: String): Map<String, String> {
        // 10 random digits.
        val nonce = (floor(Math.random() * 9_000_000_000L) + 1_000_000_000L).toLong().toString()
        val timestamp = System.currentTimeMillis().toString()
        val headerMap = mutableMapOf(
            "appKey" to appKey,
            "nonce" to nonce,
            "timestamp" to timestamp,
        )

        val msgAuthCode = Mac.getInstance(algorithm).apply {
            val secretKey = SecretKeySpec(appSecret.toByteArray(), algorithm)
            init(secretKey)

            val payload = "${appKey}${nonce}${timestamp}${body}"
            update(payload.toByteArray())
        }.doFinal()

        headerMap["signed"] = msgAuthCode.toHexString()
        headerMap["Content-Type"] = "application/json"
        headerMap["Content-Length"] = "${body.length}"

        return headerMap
    }

    private fun log(string: String) {
        log.add(string)
        Log.d(TAG, "$string\n")
    }
}
