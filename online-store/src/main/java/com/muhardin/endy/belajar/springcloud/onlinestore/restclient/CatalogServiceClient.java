package com.muhardin.endy.belajar.springcloud.onlinestore.restclient;

import com.muhardin.endy.belajar.springcloud.onlinestore.dto.Product;
import java.util.List;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "catalog", fallback = CatalogServiceClientFallback.class)
public interface CatalogServiceClient {

    @RequestMapping(method = RequestMethod.GET, value = "/product", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Product> semuaProduct();
}
