package com.lns.search.service;


import com.lns.search.entity.NoticeFlat;
import com.lns.search.enums.StateCompare;

import java.io.IOException;
import java.util.List;

public interface NoticeService {

    List<NoticeFlat> getNoticeFlats(String chatId) throws IOException;

    List<NoticeFlat> compareActualFlats(String chatId, StateCompare stateCompare) throws IOException;
}
