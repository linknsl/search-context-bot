package com.lns.search.service;


import com.lns.search.entity.NoticeFlat;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface JsopNoticeService {

    List<NoticeFlat> getNoticeFlatsFromOut(String chatId) throws IOException;

    LocalDateTime localDateAndZone();
}
