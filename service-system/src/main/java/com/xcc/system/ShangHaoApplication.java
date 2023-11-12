package com.xcc.system;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xcc.system.mapper")
public class ShangHaoApplication {

    public static void main(String[] args){
        SpringApplication.run(ShangHaoApplication.class,args);
    }
}
