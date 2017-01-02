package com.muhardin.endy.belajar.springcloud.catalog.controller;

import com.muhardin.endy.belajar.springcloud.catalog.dao.ProductDao;
import com.muhardin.endy.belajar.springcloud.catalog.entity.Product;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    @Autowired private ProductDao productDao;
    
    @RequestMapping(method = RequestMethod.GET,value = "/product")
    public List<Product> semuaProduct(){
        return productDao.findAll(new PageRequest(0, 10000)).getContent();
    }
}
