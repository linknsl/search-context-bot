package com.lns.search.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Params {
    String description;
    String square;
}
