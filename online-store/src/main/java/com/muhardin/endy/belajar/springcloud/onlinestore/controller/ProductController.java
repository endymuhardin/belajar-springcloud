package com.muhardin.endy.belajar.springcloud.onlinestore.controller;

import com.muhardin.endy.belajar.springcloud.onlinestore.dto.Product;
import com.muhardin.endy.belajar.springcloud.onlinestore.restclient.CatalogServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
public class ProductController {
    
    @Autowired
    private CatalogServiceClient catalogServiceClient;
    
    @RequestMapping("/list")
    public ModelMap semuaProduk(Pageable page){
        Integer pageNumber = page.getPageNumber();
        Integer pageSize = page.getPageSize();
        Page<Product> hasil = catalogServiceClient.semuaProduct(pageNumber, pageSize);
        System.out.println("Jumlah data : "+hasil.getTotalElements());
        return new ModelMap("daftarProduk", hasil);
    }
}
