package com.satya.demo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PropertiesReader {
    @Value("${my.name.is}")
    private String myNameFromPropertyFile;
    @Value("${age}")
    private int myAgeFromPropertyFile;
    @Value("${profile.in.use}")
    private String profileInUse;
}
