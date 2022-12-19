package com.lns.search.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NoticeFlatDto {

    private Integer flatId;
    private String link;
    private String state;
    private String description;
    private LocalDateTime createDate;
    private Integer count_view;
    private Integer chatId;
}

