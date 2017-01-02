package com.muhardin.endy.belajar.springcloud.onlinestore.restclient;

import com.muhardin.endy.belajar.springcloud.onlinestore.dto.Product;
import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class CatalogServiceClientFallback implements CatalogServiceClient {

    @Override
    public Page<Product> semuaProduct(Pageable page) {
        return new PageImpl<>(new ArrayList());
    }

}