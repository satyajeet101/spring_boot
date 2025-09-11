package com.satya.demo.profiles;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class ProfileExampleDev {
    @PostConstruct
    public void dispaly(){
        System.out.println("From DEV profile....");
    }
}
