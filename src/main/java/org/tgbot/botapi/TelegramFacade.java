package org.tgbot.botapi;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.tgbot.BotConfig;
import org.tgbot.EmailAlertBot;
import org.tgbot.botapi.keyboards.AssignChatUserKeyboard;
import org.tgbot.botapi.keyboards.AssignCommandKeyboard;
import org.tgbot.botapi.keyboards.ShowAssignedUserKeyboard;
import org.tgbot.cache.InMemoryChatCache;
import org.tgbot.dao.model.Chat;
import org.tgbot.dao.service.*;
import org.tgbot.dao.model.*;
import org.tgbot.service.ReplyMessagesService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.tgbot.utils.Emojis;


import java.util.List;
import java.util.Optional;

/**
 * @author Sergei Viacheslaev
 */
@Slf4j
@Setter
@Component
public class TelegramFacade {
    @Autowired
    private BotStateContext botStateContext;
    @Autowired
    private InMemoryChatCache inMemoryUserCache;
    @Lazy
    @Autowired
    private EmailAlertBot emailAlertBot;
    @Autowired
    private BotConfig botConfig;
    @Autowired
    private ReplyMessagesService messagesService;
    @Autowired
    private ChatDataService chatDataService;
    @Autowired
    private UserDataService userDataService;


    public Optional<SendMessage> handleUpdate(Update update) throws TelegramApiException {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
            log.debug(update.getCallbackQuery().toString());
            processCallbackQuery(callbackQuery);
        }


        Message message = update.getMessage();
        if (message != null && message.hasText()){
            log.info("New message from User:{}, userId: {}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getFrom().getId(), message.getChatId(), message.getText());
            log.debug(message.toString());
            replyMessage = handleInputMessage(message);
        }

        return Optional.ofNullable(replyMessage);
    }


    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        long chatId = message.getChatId();
        BotState botState;
        SendMessage replyMessage = null;
        Chat chat;
        switch (inputMsg) {
            case "/start":
                botState = BotState.ACTIVATE;
                break;
            case "/stop":
                botState = BotState.DEACTIVATE;
                break;
            case "/add_user":
                botState = BotState.ADD_USER;
                break;
            case "/del_user":
                botState = BotState.DELETE_USER;
                break;
            case "/add_subs":
                botState = BotState.ADD_SUBS;
                break;
            case "/del_subs":
                botState = BotState.DELETE_SUBS;
                break;
            case "/my_subs":
                botState = BotState.LIST_SUBS;
                break;
            default:
                botState = inMemoryUserCache.getUsersCurrentBotState(chatId);
                chat = chatDataService.findChatOrNew(chatId);
                if (chat.isActivated()
                        && !botState.equals(BotState.ADDING_USER)
                        && !botState.equals(BotState.DELETING_USER)
                        && !botState.equals(BotState.ADDING_SUBS)
                        && !botState.equals(BotState.DELETING_SUBS)
                        && !botState.equals(BotState.ACTIVATE)) {
                    botState = BotState.WORKING;
                }
                break;
        }
        inMemoryUserCache.setUsersCurrentBotState(chatId, botState);

        Optional<SendMessage> optionalReply = botStateContext.processInputMessage(botState, message);

        if (optionalReply.isPresent()){
            return optionalReply.get();
        }


        return replyMessage;
    }

    private void processCallbackQuery(CallbackQuery buttonQuery) throws TelegramApiException {
        final long chatId = buttonQuery.getMessage().getChatId();
        final int messageId = buttonQuery.getMessage().getMessageId();

        if (buttonQuery.getData() == null) {
            emailAlertBot.editMessageReplyMarkup(chatId, messageId, new AssignCommandKeyboard());
            return;
        }

        boolean dataIsPullName = botConfig.getAssignPulls().stream().anyMatch(buttonQuery.getData()::equalsIgnoreCase);
        if (dataIsPullName) {
            List<ChatUser> chatUsers = chatDataService.findChatOrNew(chatId).getChatUsers();
            emailAlertBot.editMessageReplyMarkup(chatId, messageId,
                    new AssignChatUserKeyboard(buttonQuery.getData(), chatUsers));
            return;
        }

        ChatUser user = userDataService.getUserById(buttonQuery.getData());
        if (user != null) {
            emailAlertBot.editMessageReplyMarkup(chatId, messageId, new ShowAssignedUserKeyboard(user));
            String messageText = messagesService.getReplyText("reply.messageAssigned", user.getName());
            emailAlertBot.sendReplyMessage(chatId, messageText, messageId);
        }

    }

}

