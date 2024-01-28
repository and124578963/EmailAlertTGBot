package org.tgbot.utils;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum Emojis {

    SEPARATOR(EmojiParser.parseToUnicode(":thought_balloon:")), // 💭
    ARROW_BACK(EmojiParser.parseToUnicode(":arrow_left:")); // ↩

    private final String emojiName;

    @Override
    public String toString() {
        return emojiName;
    }
}
