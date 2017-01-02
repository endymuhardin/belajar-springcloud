package com.muhardin.endy.belajar.springcloud.onlinestore;

import com.fasterxml.jackson.databind.Module;
import com.muhardin.endy.belajar.springcloud.onlinestore.helper.SpringDataPageJacksonDeserializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.dialect.springdata.SpringDataDialect;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class OnlineStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineStoreApplication.class, args);
    }
    
    @Bean
    public Module jacksonPageDeserializer() {
        return new SpringDataPageJacksonDeserializer();
    }
    
    @Bean
    public SpringDataDialect springDataDialect() {
        return new SpringDataDialect();
    }
}
