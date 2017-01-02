package com.muhardin.endy.belajar.springcloud.onlinestore.helper;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


@JsonDeserialize(as = SimplePageImpl.class)
public interface PageMixIn{ }