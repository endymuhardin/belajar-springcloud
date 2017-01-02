package com.muhardin.endy.belajar.springcloud.onlinestore.restclient;

import com.muhardin.endy.belajar.springcloud.onlinestore.dto.Product;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "catalog", fallback = CatalogServiceClientFallback.class)
public interface CatalogServiceClient {

    @RequestMapping(method = RequestMethod.GET, value = "/product")
    public Page<Product> semuaProduct(Pageable page);
}
