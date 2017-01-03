package com.muhardin.endy.belajar.springcloud.onlinestore.controller;

import com.muhardin.endy.belajar.springcloud.onlinestore.dto.Product;
import com.muhardin.endy.belajar.springcloud.onlinestore.dto.ProductPhoto;
import com.muhardin.endy.belajar.springcloud.onlinestore.restclient.CatalogServiceClient;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProductController {
    
    @Autowired
    private CatalogServiceClient catalogServiceClient;
    
    @RequestMapping("/product/list")
    public ModelMap semuaProduk(Pageable page){
        return new ModelMap("daftarProduk", catalogServiceClient.semuaProduct(page));
    }
    
    @RequestMapping("/{permalink}")
    public String productByPermalink(@PathVariable String permalink, ModelMap mm){
        Product p = catalogServiceClient.findBySlug(permalink);
        if(p != null){
            mm.addAttribute(p);
            List<ProductPhoto> photos = catalogServiceClient.findPhotoForProduct(p.getId());
            System.out.println("Jumlah foto  : " +photos.size());
            mm.addAttribute("daftarFoto", photos);
        }
        return "/product/detail";
    }
}
