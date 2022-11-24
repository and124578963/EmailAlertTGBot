package org.telegram.cache;

import org.springframework.stereotype.Component;
import org.telegram.botapi.BotState;
import org.telegram.telegrambots.meta.api.objects.Message;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;


/**
 * In-memory cache.
 * usersBotStates: user_id and user's bot state
 * usersProfileData: user_id  and user's profile data.
 */

@Component
public class UserDataCache implements DataCache {
    private Map<Long, BotState> usersBotStates = new HashMap<>();
    private Map<Long, String> usersSelectedPull = new HashMap<>();
    private Map<Long, List<Message>> listBotSendedMessage = new HashMap<>();
    private Map<Long, List<Message>> listBotSendedMessageForDel = new HashMap<>();

    private Map<Long, String > usersAddMusicData = new HashMap<>();

    private Callable<Object> sendDocument;


    @Override
    public void setUsersCurrentBotState(long userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(long userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.NO_STATE;
        }

        return botState;
    }

    public List<Message> getBotSendedMessage(long userId) {
        List<Message> list = listBotSendedMessage.get(userId);
        if (list == null){
            list = new ArrayList<>();
        }
        listBotSendedMessage.put(userId, new ArrayList<>());
        return list;
    }


    public void setBotSendedMessage(long userId, Message message) {
        List<Message> messageList = listBotSendedMessage.get(userId);
        if (messageList == null){
            messageList = new ArrayList<>();
        }
        messageList.add(message);
        listBotSendedMessage.put(userId, messageList);
    }
    public void moveMessageToDelStack(long userId){
        listBotSendedMessageForDel.put(userId, getBotSendedMessage(userId));
    }

    public List<Message> getMessageForDel(long userId) {
        List<Message> list = listBotSendedMessageForDel.get(userId);
        if (list == null){
            list = new ArrayList<>();
        }
        listBotSendedMessageForDel.put(userId, new ArrayList<>());
        return list;
    }

}
