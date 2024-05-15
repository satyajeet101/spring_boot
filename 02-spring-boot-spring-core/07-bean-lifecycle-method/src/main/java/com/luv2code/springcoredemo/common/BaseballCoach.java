package com.luv2code.springcoredemo.common;

import org.springframework.stereotype.Component;
@Component
public class BaseballCoach implements Coach{
    @Override
    public String getDailyWorkout(){
        return "Spent 30 min in BASE BAT practice";
    }
}