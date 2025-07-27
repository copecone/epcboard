package io.github.copecone.epcboard.util.random.internal

import io.github.copecone.epcboard.Environment
import io.github.copecone.epcboard.util.random.DoubleRange
import io.github.copecone.epcboard.util.random.FloatRange
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger

private object RandomOrgJsonRPCUtil {
    val logger = LoggerFactory.getLogger(RandomOrgJsonRPCUtil::class.simpleName)
    val apiURL = "https://api.random.org/json-rpc/4/invoke"

    val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 3000
        }
    }

    @Serializable
    @OptIn(ExperimentalSerializationApi::class)
    data class JsonRPCRequest(
        @EncodeDefault(EncodeDefault.Mode.ALWAYS) @SerialName("jsonrpc") val jsonRPC: String = "2.0",
        val method: String,
        val params: JsonObject,
        val id: Int
    )

    @Serializable
    @OptIn(ExperimentalSerializationApi::class)
    data class JsonRPCResponse(
        @EncodeDefault(EncodeDefault.Mode.ALWAYS) @SerialName("jsonrpc") val jsonRPC: String = "2.0",
        @EncodeDefault(EncodeDefault.Mode.NEVER) val result: JsonObject? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val error: JsonObject? = null,
        val id: Int
    )

    var callID = AtomicInteger(0)
    val jsonBuild = Json { ignoreUnknownKeys = true }
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun callMethod(method: String, params: JsonObject): JsonElement {
        val idStorage = callID.andIncrement

        val response = client.post(apiURL) {
            val requestData = JsonRPCRequest(
                method = method,
                params = params,
                id = idStorage
            )

            logger.debug(requestData.toString())
            setBody(jsonBuild.encodeToString(requestData))

            contentType(ContentType.Application.Json)
        }

        val responseText = response.bodyAsText()
        logger.debug(responseText)

        val responseData = Json.decodeFromString<JsonRPCResponse>(responseText)

        if (responseData.id != idStorage) throw UnknownError()
        return responseData.result!!
    }
}

internal object RandomOrgAPI {
    const val FLOAT_PRECISION = 7
    const val DOUBLE_PRECISION = 14

    val validIntegerRange: IntRange =
        -100_000_000..100_000_000

    suspend fun generateInt(
        num: Int, range: IntRange
    ): Array<Int> {
        if (range.start < validIntegerRange.start || range.endInclusive > validIntegerRange.endInclusive) throw IllegalArgumentException()

        val useHex = range.start >= 0
        val apiCallResponse = RandomOrgJsonRPCUtil.callMethod("generateIntegers", buildJsonObject {
            put("apiKey", Environment.randomOrgAPIKey)
            put("n", num)
            put("min", range.start)
            put("max", range.endInclusive)
            put("replacement", true) // Duplicate values are allowed
            put("base", if (useHex) 16 else 10) // Reducing traffics via using the highest base supported
        })

        val apiResponseData = apiCallResponse.jsonObject
        return apiResponseData["random"]!!.jsonObject["data"]!!.jsonArray.map {
            if (useHex) it.jsonPrimitive.content.toInt(16) else it.jsonPrimitive.int
        }.toTypedArray()
    }

    suspend fun generateFloat(
        num: Int, range: FloatRange
    ): Array<Float> {
        val apiCallResponse = RandomOrgJsonRPCUtil.callMethod("generateDecimalFractions", buildJsonObject {
            put("apiKey", Environment.randomOrgAPIKey)
            put("n", num)
            put("decimalPlaces", FLOAT_PRECISION)
            put("replacement", true) // Duplicate values are allowed
        })

        val scale = range.endInclusive - range.start
        val apiResponseData = apiCallResponse.jsonObject
        return apiResponseData["random"]!!.jsonObject["data"]!!.jsonArray.map {
            it.jsonPrimitive.float * scale + range.start
        }.toTypedArray()
    }

    suspend fun generateDouble(
        num: Int, range: DoubleRange
    ): Array<Double> {
        val apiCallResponse = RandomOrgJsonRPCUtil.callMethod("generateDecimalFractions", buildJsonObject {
            put("apiKey", Environment.randomOrgAPIKey)
            put("n", num)
            put("decimalPlaces", DOUBLE_PRECISION)
            put("replacement", true) // Duplicate values are allowed
        })

        val scale = range.endInclusive - range.start
        val apiResponseData = apiCallResponse.jsonObject
        return apiResponseData["random"]!!.jsonObject["data"]!!.jsonArray.map {
            it.jsonPrimitive.double * scale + range.start
        }.toTypedArray()
    }
}