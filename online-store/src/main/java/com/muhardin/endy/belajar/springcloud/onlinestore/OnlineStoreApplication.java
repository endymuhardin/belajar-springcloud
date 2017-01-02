package com.muhardin.endy.belajar.springcloud.onlinestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class OnlineStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineStoreApplication.class, args);
    }
}
