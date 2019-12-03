package com.haz.client

import io.ktor.client.HttpClient
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class FeignAutoConfiguration {
    @Bean
    public open fun httpClient(): CloseableHttpClient {
        return HttpClientBuilder.create().build()
    }

    @Bean
    public open fun ktorHttpClient(): HttpClient {
        return HttpClient()
    }
}