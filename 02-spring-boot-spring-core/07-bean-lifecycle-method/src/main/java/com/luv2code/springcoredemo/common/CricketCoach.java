package com.luv2code.springcoredemo.common;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class CricketCoach implements Coach{
    //Define init method
    @PostConstruct
    public void doMyStartupStuff(){
        System.out.println("In do my doMyStartupStuff(): "+getClass().getSimpleName());
    }
    //Define destroy method
    @PreDestroy
    public void doMyCleanupStuff(){
        System.out.println("In do my doMyCleanupStuff(): "+getClass().getSimpleName());
    }
    @Override
    public String getDailyWorkout(){
        return "Practice fast bowling for 15 minutes!!";
    }
}
