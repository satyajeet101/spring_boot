package com.luv2code.springcoredemo.rest;

import com.luv2code.springcoredemo.common.Coach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    //Define a private field for dependency
    private Coach myCoach;
    private Coach theCoach;
    @Autowired
    DemoController(@Qualifier("cricketCoach") Coach myCoach,
                   @Qualifier("cricketCoach") Coach theCoach){
        this.myCoach = myCoach;
        this.theCoach = theCoach;
    }
    @GetMapping("/dailyworkout")
    public String getDailyWorkout(){
        return myCoach.getDailyWorkout();
    }
    @GetMapping("/check")
    public String check(){
        return "Comparing beans: myCoach == theCoach, "+(myCoach==theCoach);
    }
}
