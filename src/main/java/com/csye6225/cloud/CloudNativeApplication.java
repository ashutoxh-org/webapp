package com.csye6225.cloud;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The type Cloud native application.
 */
@SpringBootApplication
public class CloudNativeApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        LoggerFactory.getLogger(CloudNativeApplication.class).info("======== Application started ========");
        SpringApplication.run(CloudNativeApplication.class, args);
    }

}
