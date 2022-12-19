package com.lns.search;

import com.lns.search.service.StoreClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients = {StoreClient.class})
public class SearchFlatApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchFlatApplication.class, args);
    }

}