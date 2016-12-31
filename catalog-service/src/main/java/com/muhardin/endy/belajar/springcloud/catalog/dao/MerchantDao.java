package com.muhardin.endy.belajar.springcloud.catalog.dao;

import com.muhardin.endy.belajar.springcloud.catalog.entity.Merchant;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MerchantDao extends PagingAndSortingRepository<Merchant, String>{
    
}
