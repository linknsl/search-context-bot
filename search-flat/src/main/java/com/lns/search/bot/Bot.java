package com.lns.search.bot;

import com.lns.search.entity.NoticeFlat;
import com.lns.search.enums.Command;
import com.lns.search.enums.State;
import com.lns.search.repository.NoticeFlatRepository;
import com.lns.search.service.NoticeService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.lns.search.enums.StateCompare.ALWAYS;
import static com.lns.search.enums.StateCompare.FIRST;
import static com.lns.search.util.TelegramUtil.createMessageTemplate;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {

    @Value("${app.version}")
    String version;

    @Value("${telegram.bot.name}")
    @Getter
    private String botUsername;

    @Value("${telegram.bot.token}")
    @Getter
    private String botToken;

    private String chatId;

    private final NoticeService noticeService;

    private final NoticeFlatRepository noticeFlatRepository;

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        Message response = Objects.nonNull(update.getMessage()) ? update.getMessage() : update.getChannelPost();
        List<SendMessage> listMessage = new ArrayList<>();
        String out = "";
        val cmd = Command.of(response.getText());
        if (Objects.nonNull(cmd)) {
            switch (cmd) {
                case START:
                    out = "Старт мониторинга relase " + version;
                    this.chatId = response.getChatId().toString();
                    listMessage = getMessage(noticeService.compareActualFlats(chatId, FIRST));
                    break;
                case STOP:
                    out = "Мониторинг завершен";
                    this.chatId = null;
                    break;
                case LAST:
                    listMessage = getMessage(Collections.singletonList(noticeFlatRepository.findByStateIsAndOrderBy(State.ACTUAL, 0)));
                    break;
                case ALIVE:
                    if (Objects.nonNull(chatId)) {
                        out = "Yes I Alive";
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + cmd);
            }
        }
        sendMesssages(listMessage, chatId, out);
    }

    @NotNull
    private List<SendMessage> getMessage(List<NoticeFlat> newFlat) {
        return newFlat.stream()
                .map(v -> createMessageTemplate(chatId,
                        "Впервые: "
                                + v.getCreateDate().format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yy"))
                                + "\n" + "Просмотры: "
                                + v.getCountView()
                                + "\n" + "Площадь: "
                                + v.getSquare()
                                + "\n" + "Кол обновл: "
                                + (Objects.nonNull(v.getCountUpdate()) ? v.getCountUpdate() : "0")
                                + "\n" + v.getLink())
                )
                .collect(Collectors.toList());
    }

    private void sendMesssages(List<SendMessage> listMessage, String chatId, String s) {
        if (!s.isEmpty()) {
            listMessage.add(createMessageTemplate(chatId, s));
        }
        log.info(s);
        exucuteListMessage(listMessage);
    }

    private void exucuteListMessage(List<SendMessage> messages) {
        messages.forEach(v -> {
            try {
                execute(v);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
    }

    // evry minuts
    @Scheduled(cron = "0 * * * * *")
    public void customScheduler() {
        try {
            // do what ever you want to run repeatedly
            if (Objects.nonNull(chatId)) {
                val result = getMessage(noticeService.compareActualFlats(chatId, ALWAYS));
                if (!result.isEmpty()) {
                    exucuteListMessage(result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}