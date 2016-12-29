package com.muhardin.endy.belajar.springcloud;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class CatalogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogServiceApplication.class, args);
    }
    
    @Value("${nama:Dummy Customer}")
    private String nama;
    
    @RequestMapping("/halo")
    public Map<String, String> halo(){
        Map<String, String> data = new HashMap<>();
        data.put("nama", nama);
        data.put("waktu", new Date().toString());
        return data;
    }
}
