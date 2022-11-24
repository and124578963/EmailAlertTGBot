package org.telegram.utils;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum Emojis {
    SEPARATOR(EmojiParser.parseToUnicode(":thought_balloon:")),
    ;



    private String emojiName;

    @Override
    public String toString() {
        return emojiName;
    }
}
