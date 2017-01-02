package com.muhardin.endy.belajar.springcloud.catalog.controller;

import com.muhardin.endy.belajar.springcloud.catalog.dao.ProductDao;
import com.muhardin.endy.belajar.springcloud.catalog.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    @Autowired private ProductDao productDao;
    
    @RequestMapping(method = RequestMethod.GET,value = "/product")
    public Page<Product> semuaProduct(Pageable page){
        return productDao.findAll(page);
    }
}
