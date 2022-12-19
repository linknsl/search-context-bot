package com.lns.search.controller;


import com.lns.search.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class InfoController {

    private final NoticeService noticeService;

    @GetMapping
    public Object getNoticeInfo() throws IOException {
        return noticeService.getNoticeFlats("123456");
    }
}
