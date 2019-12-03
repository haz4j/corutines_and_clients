package com.haz.client

import com.haz.server.FirstApi
import feign.Feign
import feign.httpclient.ApacheHttpClient
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@AutoConfigureAfter(FeignConfiguration::class)
public open class FeignConfiguration {

    @Bean
    public open fun firstApi(): FirstApi {
        val client = ApacheHttpClient()
        return Feign.builder()
                .client(client)
                .target(FirstApi::class.java, "http://localhost:8080/first_controller")
    }
}