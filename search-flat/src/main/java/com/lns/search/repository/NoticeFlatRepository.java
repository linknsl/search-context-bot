package com.lns.search.repository;

import com.lns.search.entity.NoticeFlat;
import com.lns.search.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeFlatRepository extends JpaRepository<NoticeFlat, Long> {
    List<NoticeFlat> findAllByStateIs(State status);

    NoticeFlat findByStateIsAndOrderBy(State status, Integer value);
}
