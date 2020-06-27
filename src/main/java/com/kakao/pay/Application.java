package com.kakao.pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String arg[]){
        new MongoConfig().init();
        SpringApplication.run(Application.class, arg);
    }
}

