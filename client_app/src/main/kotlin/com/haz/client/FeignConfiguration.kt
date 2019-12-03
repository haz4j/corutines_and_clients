package com.haz.client

import com.haz.server.FirstApi
import feign.Feign
import feign.httpclient.ApacheHttpClient
import io.ktor.client.HttpClient
import org.apache.http.impl.client.CloseableHttpClient
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@AutoConfigureAfter(FeignConfiguration::class)
public open class FeignConfiguration {

    @Bean
    public open fun firstApi(httpClient: CloseableHttpClient): FirstApi {
        return Feign.builder()
                .client(ApacheHttpClient(httpClient))
                .target(FirstApi::class.java, "http://localhost:8080/first_controller")
    }

    @Bean
    public open fun ktorFirstApi(ktorHttpClient: HttpClient): FirstApi {
        return Feign.builder()
                .client(KtorApacheHttpClient(ktorHttpClient))
                .target(FirstApi::class.java, "http://localhost:8080/first_controller")
    }
}