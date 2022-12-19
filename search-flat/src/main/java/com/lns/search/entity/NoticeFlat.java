package com.lns.search.entity;

import com.lns.search.enums.State;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Data
@Table(name = "notice_flat")
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class NoticeFlat {

    @Id
    @Column(name = "notice_id")
    private String noticeId;

    @Column(name = "link")
    private String link;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "square")
    private String square;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "sort_date")
    private LocalDateTime sortDate;

    @Column(name = "count_view")
    private Integer countView;

    @Column(name = "count_update")
    private Integer countUpdate;

    @Column(name = "chat_id")
    private String chatId;

    @Column(name = "order_by")
    private Integer orderBy;
}
