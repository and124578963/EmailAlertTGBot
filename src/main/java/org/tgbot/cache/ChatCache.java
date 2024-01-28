package org.tgbot.cache;

import org.tgbot.botapi.BotState;


public interface ChatCache {
    void setUsersCurrentBotState(long userId, BotState botState);

    BotState getUsersCurrentBotState(long userId);



}
