package org.tgbot.botapi.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.tgbot.BotConfig;
import org.tgbot.botapi.BotState;
import org.tgbot.cache.InMemoryChatCache;
import org.tgbot.dao.model.Chat;
import org.tgbot.dao.service.ChatDataService;
import org.tgbot.service.ReplyMessagesService;

@Component
public class ActivationHandler implements InputMessageHandler{
    @Autowired
    private InMemoryChatCache inMemoryUserCache;
    @Autowired
    private ChatDataService chatDataService;
    @Autowired
    private BotConfig botConfig;
    @Autowired
    private ReplyMessagesService messagesService;

    @Override
    public BotState getHandlerName() {
        return BotState.ACTIVATION;
    }
    @Override
    public SendMessage handle(Message message, BotState botState) {
        SendMessage replyMessage = null;
        long chatId = message.getChatId();
        String text = message.getText();
        Chat chat = chatDataService.findChatOrNew(chatId);

        switch (botState) {
            case ACTIVATE:
                if (text.equalsIgnoreCase(botConfig.getSecretPhrase())){
                    chatDataService.setActivated(chat);
                    replyMessage = messagesService.getReplyMessage(chatId,"reply.activate.isActivated");
                    inMemoryUserCache.setUsersCurrentBotState(chatId, BotState.WORKING);
                }else {
                    replyMessage = messagesService.getReplyMessage(chatId,"reply.activate.saySecret");
                }
                break;

            case DEACTIVATE:
                chatDataService.setDeactivated(chat);
                replyMessage = messagesService.getReplyMessage(chatId,"reply.activate.isDeactivated");
                inMemoryUserCache.setUsersCurrentBotState(chatId, BotState.WORKING);
        }
        return replyMessage;
    }


}
