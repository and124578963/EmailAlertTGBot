package org.tgbot.botapi.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.tgbot.botapi.BotState;
import org.tgbot.cache.InMemoryChatCache;
import org.tgbot.dao.service.ChatDataService;
import org.tgbot.service.ReplyMessagesService;

@Component
public class SubscriptionHandler implements InputMessageHandler{
    @Autowired
    private InMemoryChatCache inMemoryUserCache;
    @Autowired
    private ChatDataService chatDataService;
    @Autowired
    private ReplyMessagesService messagesService;

    @Override
    public SendMessage handle(Message message, BotState botState) {
        SendMessage replyMessage = null;
        long chatId = message.getChatId();
        String text = message.getText();

        switch (botState){
            case ADD_SUBS:
                replyMessage = messagesService.getReplyMessage(chatId, "reply.subs.addEmailFolder");
                inMemoryUserCache.setUsersCurrentBotState(chatId, BotState.ADDING_SUBS);
                break;

            case ADDING_SUBS:
                chatDataService.addSubscribe(chatId, text);
                replyMessage = messagesService.getReplyMessage(chatId, "reply.subs.folderIsAdded", text);
                inMemoryUserCache.setUsersCurrentBotState(chatId, BotState.WORKING);
                break;

            case DELETE_SUBS:
                replyMessage = messagesService.getReplyMessage(chatId, "reply.subs.emailFolderForDel");
                inMemoryUserCache.setUsersCurrentBotState(chatId, BotState.DELETING_SUBS);
                break;

            case DELETING_SUBS:
                boolean deleted = chatDataService.delSubscribe(chatId, text);
                String replyText = (deleted) ? "reply.subs.goodDeleting" : "reply.subs.badDeleting";
                replyMessage = messagesService.getReplyMessage(chatId, replyText);
                inMemoryUserCache.setUsersCurrentBotState(chatId, BotState.WORKING);
                break;

            case LIST_SUBS:
                replyMessage = new SendMessage(Long.toString(chatId),
                        chatDataService.findChatOrNew(chatId).getSubscribes().toString());
        }

        return replyMessage;

    }

    @Override
    public BotState getHandlerName() {
        return BotState.SUBSCRIPTION;
    }
}
