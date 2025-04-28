package com.satya.demo.profiles;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("qa")
public class ProfileExampleQa {
    @PostConstruct
    public void dispaly(){
        System.out.println("From QA profile....");
    }
}
