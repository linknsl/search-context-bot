package com.lns.search.bot;

import com.lns.search.BaseInitTest;
import com.lns.search.entity.NoticeFlat;
import com.lns.search.enums.State;
import com.lns.search.repository.NoticeFlatRepository;
import com.lns.search.service.JsopNoticeService;
import com.lns.search.service.NoticeService;
import lombok.val;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.TelegramBotInitializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

import static com.lns.search.enums.StateCompare.ALWAYS;
import static com.lns.search.enums.StateCompare.FIRST;
import static org.mockito.Mockito.when;

@Sql
class BotTest extends BaseInitTest {

    @Autowired
    NoticeFlatRepository noticeFlatRepository;
    @MockBean
    JsopNoticeService jsopNoticeService;
    @MockBean
    TelegramBotInitializer tg;
    @SpyBean
    NoticeService noticeService;

    @Test
    void sendInArhiveOldFlats() throws IOException {

        val index = 19;
        val resDb = noticeFlatRepository.findAllByStateIs(State.ACTUAL);
        Assert.assertEquals(State.ACTUAL, resDb.get(index).getState());
        resDb.remove(index);
        when(noticeService.getNoticeFlats("1")).thenReturn(resDb);
//        when(storeClient.getViewAd(Collections.singletonList(1l))).thenReturn(new Object());
        val res = noticeService.compareActualFlats("1", FIRST);
        Assert.assertEquals(Collections.EMPTY_LIST, res);
        Assert.assertEquals(State.ARHIVE, resDb.get(index).getState());
    }

    @Test
    void sendNewFlats() throws IOException, TelegramApiException {
        val index = 19;
        val resDb = noticeFlatRepository.findAllByStateIs(State.ACTUAL);
        val el = resDb.get(index);
        LocalDateTime date = LocalDateTime.of(2022, 1, 1, 0, 0, 0);
        Integer count = 10;
        val newNotice = NoticeFlat.builder()
                .chatId(el.getChatId())
                .createDate(date)
                .countView(count)
                .link("https://krisha.kz/a/show/1")
                .description(el.getDescription())
                .noticeId("1")
                .orderBy(1000)
                .build();
        resDb.add(newNotice);
        when(noticeService.getNoticeFlats("1")).thenReturn(resDb);
        val res = noticeService.compareActualFlats("1", ALWAYS);

        Assert.assertEquals(res.get(0).getCountView(), newNotice.getCountView());
        Assert.assertEquals(res.get(0).getLink(), newNotice.getLink());
        Assert.assertEquals(res.get(0).getDescription(), el.getDescription());
        Assert.assertEquals(res.get(0).getNoticeId(), newNotice.getNoticeId());
    }
}