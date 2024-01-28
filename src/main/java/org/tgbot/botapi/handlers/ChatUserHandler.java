package org.tgbot.botapi.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.tgbot.BotConfig;
import org.tgbot.botapi.BotState;
import org.tgbot.cache.InMemoryChatCache;
import org.tgbot.dao.service.ChatDataService;
import org.tgbot.service.ReplyMessagesService;

@Component
public class ChatUserHandler implements InputMessageHandler{
    @Autowired
    private InMemoryChatCache inMemoryUserCache;
    @Autowired
    private ChatDataService chatDataService;
    @Autowired
    private ReplyMessagesService messagesService;
    @Autowired
    private BotConfig botConfig;
    @Override
    public SendMessage handle(Message message, BotState botState) {

        SendMessage replyMessage = null;
        long chatId = message.getChatId();
        String text = message.getText();

        switch (botState){
            case ADD_USER:
                replyMessage = messagesService.getReplyMessage(chatId,"replay.user.addNew",
                        botConfig.getAssignPulls().toString());
                inMemoryUserCache.setUsersCurrentBotState(chatId, BotState.ADDING_USER);
                break;

            case ADDING_USER:
                String[] listAdding = text.split(" - ");
                if (listAdding.length == 2 && botConfig.getAssignPulls().contains(listAdding[0])) {
                    chatDataService.addChatUser(chatId, listAdding[0], listAdding[1]);
                    replyMessage = messagesService.getReplyMessage(chatId,"replay.user.newAdded");
                    inMemoryUserCache.setUsersCurrentBotState(chatId, BotState.WORKING);
                } else {
                    replyMessage = messagesService.getReplyMessage(chatId,"replay.user.addingFailed");
                }
                break;

            case DELETE_USER:
                replyMessage = messagesService.getReplyMessage(chatId,"replay.user.delete");
                inMemoryUserCache.setUsersCurrentBotState(chatId, BotState.DELETING_USER);
                break;

            case DELETING_USER:
                inMemoryUserCache.setUsersCurrentBotState(chatId, BotState.WORKING);
                boolean deleted = chatDataService.delChatUser(chatId, text);
                String replyText = (deleted) ? "replay.user.goodDelete" : "replay.user.badDelete";
                replyMessage = messagesService.getReplyMessage(chatId,replyText);
                break;
        }

        return replyMessage;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.USERS;
    }
}
