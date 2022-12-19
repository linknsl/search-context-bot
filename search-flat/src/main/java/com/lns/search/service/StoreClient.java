package com.lns.search.service;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(value = "test", url = "https://krisha.kz")
public interface StoreClient {

    @GetMapping("/a/ajaxPhones")
    Object getPhoneNumber(@RequestParam Long id);

    @GetMapping(value ="/ms/views/krisha/live/{id}/")
    Object getViewAd(@PathVariable("id") List<Long> id);
}