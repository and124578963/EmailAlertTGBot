package org.telegram.cache;

import org.telegram.botapi.BotState;
import org.telegram.telegrambots.meta.api.objects.Message;


import java.util.List;


public interface DataCache {
    void setUsersCurrentBotState(long userId, BotState botState);

    BotState getUsersCurrentBotState(long userId);



}
