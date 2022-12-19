package com.lns.search.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("polling")
public class SiteConfig {
    private Service config;

    @Data
    public static class Service {
        private String url;
    }
}
