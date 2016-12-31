package com.muhardin.endy.belajar.springcloud.catalog.dao;

import com.muhardin.endy.belajar.springcloud.catalog.entity.Product;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductDao extends PagingAndSortingRepository<Product, String>{
    
}
