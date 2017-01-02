package com.muhardin.endy.belajar.springcloud.onlinestore.controller;

import com.muhardin.endy.belajar.springcloud.onlinestore.dto.Product;
import com.muhardin.endy.belajar.springcloud.onlinestore.restclient.CatalogServiceClient;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
public class ProductController {
    
    @Autowired
    private CatalogServiceClient catalogServiceClient;
    
    @RequestMapping("/list")
    public ModelMap semuaProduk(){
        List<Product> hasil = catalogServiceClient.semuaProduct();
        System.out.println("Jumlah data : "+hasil.size());
        return new ModelMap("daftarProduk", hasil);
    }
}
