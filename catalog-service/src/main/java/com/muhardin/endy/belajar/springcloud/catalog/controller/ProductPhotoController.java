package com.muhardin.endy.belajar.springcloud.catalog.controller;

import com.muhardin.endy.belajar.springcloud.catalog.dao.ProductPhotoDao;
import com.muhardin.endy.belajar.springcloud.catalog.entity.Product;
import com.muhardin.endy.belajar.springcloud.catalog.entity.ProductPhoto;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductPhotoController {
    @Autowired private ProductPhotoDao productPhotoDao;
    
    @RequestMapping(value = "/product/{product}/photos", method = RequestMethod.GET)
    public List<ProductPhoto> semuaProductPhoto(@PathVariable("product") Product p){
        return productPhotoDao.findByProduct(p);
    }
}