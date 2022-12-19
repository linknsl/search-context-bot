package com.lns.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public abstract class BaseInitTest {

    @Autowired
    protected MockMvc mockMvc;

    protected String toJson(Object o) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(o);
    }

    protected <T> T fromJSON(final TypeReference<T> type, final String jsonPacket) {
        T data;
        try {
            // PTAI:Suppress
            data = new ObjectMapper().readValue(jsonPacket, type);
        } catch (Exception e) {
            throw new RuntimeException("error deserialization json");
        }
        return data;
    }

}
