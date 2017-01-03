package com.muhardin.endy.belajar.springcloud.onlinestore.restclient;

import com.muhardin.endy.belajar.springcloud.onlinestore.dto.Product;
import com.muhardin.endy.belajar.springcloud.onlinestore.dto.ProductPhoto;
import java.util.List;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "catalog", fallback = CatalogServiceClientFallback.class)
public interface CatalogServiceClient {

    @RequestMapping(method = RequestMethod.GET, value = "/product")
    public Page<Product> semuaProduct(Pageable page);
    
    @RequestMapping(method = RequestMethod.GET, value = "/{slug}")
    public Product findBySlug(@PathVariable("slug") String slug);
    
    @RequestMapping(value = "/product/{product}/photos", method = RequestMethod.GET)
    public List<ProductPhoto> findPhotoForProduct(@PathVariable("product") String product);
}
