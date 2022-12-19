package com.lns.search.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Command {
    START("/start"),
    ALIVE("/alive"),
    STOP("/stop"),
    LAST("/last");

    public static Command of(String val) {
        for (Command kp : Command.values()) {
            if (kp.getTitle().equals(val)) {
                return kp;
            }
        }
        return null;
    }

    private final String title;
}
