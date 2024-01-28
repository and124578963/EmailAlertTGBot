package org.tgbot.cache;

import org.springframework.stereotype.Component;
import org.tgbot.botapi.BotState;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class InMemoryChatCache implements ChatCache {
    private final Map<Long, BotState> usersBotStates = new ConcurrentHashMap<>();

    @Override
    public void setUsersCurrentBotState(long userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(long userId) {
        return usersBotStates.getOrDefault(userId, BotState.WORKING);
    }
}

