package com.haz.client

import feign.Client
import feign.Request
import feign.Response
import feign.Util
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import org.apache.http.HttpEntity
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.client.utils.URIBuilder
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.net.URISyntaxException
import java.nio.charset.Charset
import java.util.*

/**
 * This module directs Feign's http requests to Apache's
 * [HttpClient](https://hc.apache.org/httpcomponents-client-ga/). Ex.
 *
 * <pre>
 * GitHub github = Feign.builder().client(new ApacheHttpClient()).target(GitHub.class,
 * "https://api.github.com");
</pre> */
/*
 * Based on Square, Inc's Retrofit ApacheClient implementation
 */
class KtorApacheHttpClient(private val client: HttpClient) : Client {
    @Throws(IOException::class)
    override fun execute(request: Request, options: Request.Options): Response {
        val httpUriRequestBuilder: HttpRequestBuilder
        httpUriRequestBuilder = try {
            toHttpUriRequest(request, options)
        } catch (e: URISyntaxException) {
            throw IOException("URL '" + request.url() + "' couldn't be parsed into a URI", e)
        }



        val httpClientCall = client.execute(httpUriRequestBuilder)
        return toFeignResponse(httpClientCall.response, request)
    }

    @Throws(URISyntaxException::class)
    fun toHttpUriRequest(request: Request, options: Request.Options): HttpRequestBuilder {
        val requestBuilder = RequestBuilder.create(request.httpMethod().name)
        // per request timeouts
        val requestConfig = RequestConfig.custom()
                .setConnectTimeout(options.connectTimeoutMillis())
                .setSocketTimeout(options.readTimeoutMillis())
                .build()
        requestBuilder.config = requestConfig
        val uri = URIBuilder(request.url()).build()
        requestBuilder.setUri(uri.scheme + "://" + uri.authority + uri.rawPath)
        // request query params
        val queryParams = URLEncodedUtils.parse(uri, requestBuilder.charset.name())
        for (queryParam in queryParams) {
            requestBuilder.addParameter(queryParam)
        }
        // request headers
        var hasAcceptHeader = false
        for ((headerName, value) in request.headers()) {
            if (headerName.equals(ACCEPT_HEADER_NAME, ignoreCase = true)) {
                hasAcceptHeader = true
            }
            if (headerName.equals(Util.CONTENT_LENGTH, ignoreCase = true)) { // The 'Content-Length' header is always set by the Apache client and it
// doesn't like us to set it as well.
                continue
            }
            for (headerValue in value) {
                requestBuilder.addHeader(headerName, headerValue)
            }
        }
        // some servers choke on the default accept string, so we'll set it to anything
        if (!hasAcceptHeader) {
            requestBuilder.addHeader(ACCEPT_HEADER_NAME, "*/*")
        }
        // request body
        if (request.requestBody().asBytes() != null) {
            var entity: HttpEntity? = null
            entity = if (request.charset() != null) {
                val contentType = getContentType(request)
                val content = String(request.requestBody().asBytes(), request.charset())
                StringEntity(content, contentType)
            } else {
                ByteArrayEntity(request.requestBody().asBytes())
            }
            requestBuilder.entity = entity
        } else {
            requestBuilder.entity = ByteArrayEntity(ByteArray(0))
        }
        return requestBuilder.build()
    }

    private fun getContentType(request: Request): ContentType? {
        var contentType: ContentType? = null
        for ((key, values) in request.headers()) if (key.equals("Content-Type", ignoreCase = true)) {
            if (values != null && !values.isEmpty()) {
                contentType = ContentType.parse(values.iterator().next())
                if (contentType.charset == null) {
                    contentType = contentType.withCharset(request.charset())
                }
                break
            }
        }
        return contentType
    }

    @Throws(IOException::class)
    fun toFeignResponse(httpResponse: io.ktor.client.response.HttpResponse, request: Request?): Response {
        val statusCode = httpResponse.status.value
        val reason = httpResponse.status.description

        val headers: MutableMap<String, MutableCollection<String>> = HashMap()
        for (headerName in httpResponse.headers.names()) {
            val headerValues = httpResponse.headers.getAll(headerName)
            val toMutableList = headerValues?.toMutableList()?:ArrayList()
            headers[headerName] = toMutableList

        }

        return Response.builder()
                .status(statusCode)
                .reason(reason)
                .headers(headers)
                .request(request)
                .body(toFeignBody(httpResponse))
                .build()
    }

    fun toFeignBody(httpResponse: io.ktor.client.response.HttpResponse): Response.Body? {
        val entity = httpResponse.content ?: return null
        return object : Response.Body {
            override fun length(): Int {
                return if (entity.contentLength >= 0 && entity.contentLength <= Int.MAX_VALUE) entity.contentLength.toInt() else null
            }

            override fun isRepeatable(): Boolean {
                return entity.isRepeatable
            }

            @Throws(IOException::class)
            override fun asInputStream(): InputStream {
                return entity
            }

            @Throws(IOException::class)
            override fun asReader(): Reader {
                return InputStreamReader(asInputStream(), Util.UTF_8)
            }

            @Throws(IOException::class)
            override fun asReader(charset: Charset): Reader {
                Util.checkNotNull(charset, "charset should not be null")
                return InputStreamReader(asInputStream(), charset)
            }

            @Throws(IOException::class)
            override fun close() {
                EntityUtils.consume(entity)
            }
        }
    }

    companion object {
        private const val ACCEPT_HEADER_NAME = "Accept"
    }

}