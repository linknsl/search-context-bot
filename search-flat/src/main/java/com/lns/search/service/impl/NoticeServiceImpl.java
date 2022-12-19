package com.lns.search.service.impl;

import com.lns.search.entity.NoticeFlat;
import com.lns.search.enums.State;
import com.lns.search.enums.StateCompare;
import com.lns.search.repository.NoticeFlatRepository;
import com.lns.search.service.JsopNoticeService;
import com.lns.search.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.lns.search.enums.StateCompare.ALWAYS;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final JsopNoticeService jsopNoticeService;

    private final NoticeFlatRepository noticeFlatRepository;

    @Override
    public List<NoticeFlat> getNoticeFlats(String chatId) throws IOException {
        val flatList = jsopNoticeService.getNoticeFlatsFromOut(chatId);
        return flatList;
    }

    /**
     * 1 Найти список объявлений которых нет в БД но есть в входящих
     * 2 Найти список обявлений которые уже есть в БД
     * 3 Найти список которые есть в БД но нет в входящих
     *
     * @return List<SendMessage>
     */
    @Override
    public List<NoticeFlat> compareActualFlats(String chatId, StateCompare stateCompare) throws IOException {
        val flatsDB = noticeFlatRepository.findAllByStateIs(State.ACTUAL);
        List<NoticeFlat> flatListInput = getNoticeFlats(chatId);
        List<NoticeFlat> newFlat = new ArrayList<>();
        flatListInput.forEach(f -> {
            val flatDB = flatsDB.stream().filter(v -> v.getNoticeId().equals(f.getNoticeId())).findFirst();
            if (flatDB.isPresent()) {
                f.setUpdateDate(jsopNoticeService.localDateAndZone());
                f.setCountUpdate(Objects.nonNull(f.getCountUpdate()) ? f.getCountUpdate() + 1 : 1);
                f.setCreateDate(flatDB.get().getCreateDate());
            } else {
                f.setCreateDate(jsopNoticeService.localDateAndZone());
                newFlat.add(f);
            }
        });
        saveArhiveFlats(flatsDB, flatListInput);

        if (!newFlat.isEmpty() && stateCompare.equals(ALWAYS)) {
            return newFlat;
        } else {
            return new ArrayList<>();
        }
    }

    private void saveArhiveFlats(List<NoticeFlat> flatsDB, List<NoticeFlat> flatListInput) {
        flatsDB.forEach(f -> {
            val el = flatListInput.stream().filter(v -> v.getNoticeId().equals(f.getNoticeId())).findFirst();
            if (!el.isPresent()) {
                f.setState(State.ARHIVE);
                flatListInput.add(f);
            }
        });
        noticeFlatRepository.saveAll(flatListInput);
    }
}
