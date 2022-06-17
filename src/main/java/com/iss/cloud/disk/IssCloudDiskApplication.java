package com.iss.cloud.disk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@MapperScan("com.iss.cloud.disk.dao")
public class IssCloudDiskApplication {

    public static void main(String[] args) {
        SpringApplication.run(IssCloudDiskApplication.class, args);
    }


}


