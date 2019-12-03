package com.haz.client

import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignAutoConfiguration {
    @Bean
    public fun httpClient(): CloseableHttpClient {
        return HttpClientBuilder.create().build()
    }
}