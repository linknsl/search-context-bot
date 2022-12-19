package com.lns.search.service.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lns.search.entity.NoticeFlat;
import com.lns.search.enums.State;
import com.lns.search.model.Params;
import com.lns.search.service.JsopNoticeService;
import com.lns.search.service.StoreClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JsopNoticeServiceImpl implements JsopNoticeService {

/*    @Value("${krisha.url}")
    @Getter
    private String findUrl;*/

    @Value("${polling.config.url}")
    @Getter
    private String findUrl;

    private final StoreClient storeClient;

    @Override
    public List<NoticeFlat> getNoticeFlatsFromOut(String chatId) throws IOException {
        Document doc = Jsoup.connect(findUrl).get();
        List<NoticeFlat> flatList = new ArrayList<>();
        val headlines = doc.select("a[href]");
        for (Element headline : headlines) {
            if (headline.attr("target").equals("_blank") && headline.hasText() && headline.attr("href").startsWith("/a/show")) {
                Matcher matcher = Pattern.compile("\\d+").matcher(headline.attr("href"));
                while (matcher.find()) {
                    Params params = extractAdditionalParams(matcher.group());
                    val noticeFlat = new NoticeFlat();
                    noticeFlat.setChatId(chatId);
                    noticeFlat.setSortDate(localDateAndZone());
                    noticeFlat.setLink(headline.absUrl("href"));
                    noticeFlat.setNoticeId(matcher.group());
                    noticeFlat.setState(State.ACTUAL);
                    noticeFlat.setSquare(params.getSquare());
                    noticeFlat.setDescription(params.getDescription());
                    flatList.add(noticeFlat);
                }
            }
        }
        headlines.clear();
        log.info(String.format("На сайте krisha.kz найдено %s записей", flatList.size()));

        List<Long> Ids = flatList.stream().map(NoticeFlat::getNoticeId).map(Long::valueOf).collect(Collectors.toList());
        Object res = storeClient.getViewAd(Ids);
        if (Objects.nonNull(res)) {
            JsonNode node = getJsonNode(res);
            flatList.forEach(v -> v.setCountView(node.get("data").get(v.getNoticeId()).get("nb_views").asInt()));
        }

        AtomicInteger cnt = new AtomicInteger();
        val result = flatList.stream()
                .sorted(Comparator.comparing(NoticeFlat::getSortDate).reversed())
                .collect(Collectors.toList());
        result.forEach(v -> v.setOrderBy(cnt.getAndIncrement()));
        return result;
    }

    private Params extractAdditionalParams(String idNotice) throws IOException {
        Document document = Jsoup.connect("https://krisha.kz/a/show/" + idNotice).get();
        val div = document.select("div");
        String description = "";
        String square = "";
        for (Element headline : div) {
            if (headline.attr("class").equals("offer__info-item") && headline.hasText()) {
                if (getaClass(headline, "offer__info-title").get().textNodes().get(0).toString().equals("\nПлощадь, м²")) {
                    square = getaClass(headline, "offer__advert-short-info").get().textNodes().get(0).toString();
                }
            }
            if (headline.attr("class").equals("a-text a-text-white-spaces") && headline.hasText()) {
                description = headline.textNodes().get(0).toString();
            }
        }
        return Params.builder()
                .square(square)
                .description(description)
                .build();
    }

    @NotNull
    private Optional<Element> getaClass(Element headline, String s) {
        return headline.getAllElements().stream()
                .filter(v -> v.attr("class").equals(s) && v.hasText())
                .findFirst();
    }

    @Override
    public LocalDateTime localDateAndZone() {
        Instant instant = (new Date()).toInstant();
        return instant.atZone(ZoneId.of("Asia/Kashgar")).toLocalDateTime();
    }

    private JsonNode getJsonNode(Object res) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(res);
        JsonFactory factory = mapper.getFactory();
        JsonParser jsonParser = factory.createParser(json);
        return mapper.readTree(jsonParser);
    }
}
