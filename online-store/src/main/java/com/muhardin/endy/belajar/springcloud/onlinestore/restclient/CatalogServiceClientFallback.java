package com.muhardin.endy.belajar.springcloud.onlinestore.restclient;

import com.muhardin.endy.belajar.springcloud.onlinestore.dto.Product;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CatalogServiceClientFallback implements CatalogServiceClient {

    @Override
    public List<Product> semuaProduct() {
        return new ArrayList<>();
    }

}