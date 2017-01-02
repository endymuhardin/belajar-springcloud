package com.muhardin.endy.belajar.springcloud.onlinestore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
public class ProductController {
    
    @RequestMapping("/list")
    public ModelMap semuaProduk(){
        ModelMap mm = new ModelMap();
        return mm;
    }
}
