package com.muhardin.endy.belajar.springcloud.onlinestore.helper;

import com.fasterxml.jackson.databind.Module.SetupContext;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.data.domain.Page;

public class SpringDataPageJacksonDeserializer extends SimpleModule {

    @Override
    public void setupModule(SetupContext context) {
        context.setMixInAnnotations(Page.class, PageMixIn.class);
    }
}
