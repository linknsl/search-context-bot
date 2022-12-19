package com.lns.search.util;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class TelegramUtil {
    // Creating template of SendMessage with enabled Markdown
    public static SendMessage createMessageTemplate(String chatId, String text) {
        return new SendMessage(chatId, text);
    }
}
