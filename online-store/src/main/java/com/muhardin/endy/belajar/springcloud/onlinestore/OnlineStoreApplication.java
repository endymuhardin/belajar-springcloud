package com.muhardin.endy.belajar.springcloud.onlinestore;

import com.fasterxml.jackson.databind.Module;
import com.muhardin.endy.belajar.springcloud.onlinestore.helper.PageableQueryEncoder;
import com.muhardin.endy.belajar.springcloud.onlinestore.helper.SpringDataPageJacksonDeserializer;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.dialect.springdata.SpringDataDialect;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class OnlineStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineStoreApplication.class, args);
    }
    
    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    public Encoder feignEncoder() {
        return new PageableQueryEncoder(new SpringEncoder(messageConverters));
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
