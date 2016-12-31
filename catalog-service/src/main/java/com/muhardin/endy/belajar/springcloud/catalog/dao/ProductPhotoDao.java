package com.muhardin.endy.belajar.springcloud.catalog.dao;

import com.muhardin.endy.belajar.springcloud.catalog.entity.Product;
import com.muhardin.endy.belajar.springcloud.catalog.entity.ProductPhoto;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductPhotoDao extends PagingAndSortingRepository<ProductPhoto, String>{
    public List<ProductPhoto> findByProduct(Product p);
}
