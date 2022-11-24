package org.telegram.botapi;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.MyTelegramBot;
import org.telegram.cache.UserDataCache;
import org.telegram.dao.ChatDataService;
import org.telegram.dao.MailSubjectsDataService;
import org.telegram.dao.UserDataService;
import org.telegram.dao.model.Chat;
import org.telegram.dao.model.User;
import org.telegram.service.ReplyMessagesService;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;


import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author Sergei Viacheslaev
 */
@Component
@Slf4j
public class TelegramFacade {
    private BotStateContext botStateContext;
    private UserDataCache userDataCache;
    private MyTelegramBot myWizardBot;
    private ReplyMessagesService messagesService;

    private MailSubjectsDataService mailSubjectsDataService;

    private ChatDataService chatDataService;
    private UserDataService userDataService;


    public TelegramFacade(BotStateContext botStateContext, UserDataCache userDataCache,
                          @Lazy MyTelegramBot myWizardBot, ReplyMessagesService messagesService,
                          MailSubjectsDataService mailSubjectsDataService, ChatDataService chatDataService,
                          UserDataService userDataService) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
        this.myWizardBot = myWizardBot;
        this.messagesService = messagesService;
        this.mailSubjectsDataService = mailSubjectsDataService;
        this.chatDataService = chatDataService;
        this.userDataService = userDataService;

    }

    public SendMessage handleUpdate(Update update) {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());

            return processCallbackQuery(callbackQuery);
        }


        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, userId: {}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getFrom().getId(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);


        }

        return replyMessage;
    }


    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        long userId = message.getChatId();
        long chatId = message.getChatId();
        BotState botState;
        SendMessage replyMessage = null;
        Chat chat;
        switch (inputMsg) {
            case "/start":
                botState = BotState.NO_STATE;
                chat = chatDataService.getChat(chatId);
                chat.setUserName(message.getFrom().getUserName());
                chatDataService.saveChat(chat);
                break;
            case "Торжественно клянусь, что замышляю только шалость!":
                botState = BotState.OK;
                chat = chatDataService.getChat(chatId);
                chatDataService.setActivated(chat);
                break;
            case "/stop":
                botState = BotState.NO_STATE;
                chat = chatDataService.getChat(chatId);
                chatDataService.setDeactivated(chat);
                break;
            case "/add_user":
                botState = BotState.ADD_USER;
                break;
            case "/del_user":
                botState = BotState.DEL_USER;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(chatId);
                chat = chatDataService.getChat(chatId);
                if (chat.getActivated() == 1 && !botState.equals(BotState.ADDING_USER) && !botState.equals(BotState.DELETING_USER)){
                    botState = BotState.WORK;
                }
                break;
        }

        userDataCache.setUsersCurrentBotState(chatId, botState);

        if (botState.equals(BotState.NO_STATE)) {
            replyMessage = new SendMessage(Long.toString(chatId), "Скажите секретный код");
        } else if (botState.equals(BotState.OK)) {
            replyMessage = new SendMessage(Long.toString(chatId), "Господа Хвост, Бродяга, Лунатик и Сохатый! Представляют вам — заявки от ГПБ…");
            userDataCache.setUsersCurrentBotState(chatId, BotState.WORK);
        } else if (botState.equals(BotState.ADD_USER)) {
            replyMessage = new SendMessage(Long.toString(chatId), "Чтобы добавить человека, напишите в чат: Название команды - Имя \n\nДоступные команды: Sup и WG \n\nПример: \nSup - Андрей П. (Проблемы с оркестратором)");
            userDataCache.setUsersCurrentBotState(chatId, BotState.ADDING_USER);
        } else if (botState.equals(BotState.ADDING_USER)) {
            String[] listAdding = inputMsg.split(" - ");
            if(listAdding.length == 2 && (listAdding[0].equalsIgnoreCase("sup") ||
                    listAdding[0].equalsIgnoreCase("wg"))){
                userDataService.createUser(listAdding[0], listAdding[1]);
                replyMessage = new SendMessage(Long.toString(chatId), "Пользователь добавлен!");

            }else{
                replyMessage = new SendMessage(Long.toString(chatId), "Чет не то, попробуй еще раз( \nНе забудь /add_user");
            }
            userDataCache.setUsersCurrentBotState(chatId, BotState.WORK);

        } else if (botState.equals(BotState.DEL_USER)) {
            replyMessage = new SendMessage(Long.toString(chatId), "Напишите имя пользователя для удаления (как указано на кнопке назначения ответственности)");
            userDataCache.setUsersCurrentBotState(chatId, BotState.DELETING_USER);

        } else if (botState.equals(BotState.DELETING_USER)) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.WORK);
            userDataService.deleteUser(inputMsg);
            userDataService.deleteUser(" " + inputMsg);
            replyMessage = new SendMessage(Long.toString(chatId), "Постарался удалить пользователя, но перепроверь)");
        }

        return replyMessage;
    }

    @SneakyThrows
    private SendMessage processCallbackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final int messageId = buttonQuery.getMessage().getMessageId();

        //From AskMain choose buttons
        if (buttonQuery.getData().equals("Support") || buttonQuery.getData().equals("WorkGroup")) {
            myWizardBot.editMessageReplyMarkup(chatId, messageId, getInlineButtons(buttonQuery.getData()));
        } else if (buttonQuery.getData().equals("None") ) {
            myWizardBot.editMessageReplyMarkup(chatId, messageId, getInlineButtons());
        }
        User user = userDataService.getUserById(buttonQuery.getData());
        if (user != null){
            myWizardBot.editMessageReplyMarkup(chatId, messageId, getInlineButtonUser(user));
            SendMessage replyMessage = new SendMessage(Long.toString(chatId), "Назначена задача на " + user.getName());
            myWizardBot.execute(replyMessage);
        }

        return null;


    }


    private InlineKeyboardMarkup getInlineButtonUser(User user){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton userButton = new InlineKeyboardButton();
        userButton.setText(user.getName());
        if (user.getCommand().equals("Sup")){
            userButton.setCallbackData("Support");
        } else {
            userButton.setCallbackData("WorkGroup");
        }

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(userButton);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardMarkup getInlineButtons(String command) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonSelectedCommand = new InlineKeyboardButton();
        buttonSelectedCommand.setText(command);
        buttonSelectedCommand.setCallbackData("None");
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonSelectedCommand);
        rowList.add(keyboardButtonsRow1);

        List<User> listUsers;
        if (command.equals("Support")) {
            listUsers = userDataService.getUsersOfCommand("Sup");
        } else {
            listUsers = userDataService.getUsersOfCommand("WG");
        }

        for (User user:listUsers){
            System.out.println(user.getUserId());
            InlineKeyboardButton buttonUser = new InlineKeyboardButton();
            buttonUser.setText(user.getName());
            buttonUser.setCallbackData(user.getUserId());
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            keyboardButtonsRow.add(buttonUser);
            rowList.add(keyboardButtonsRow);
        }
        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    private InlineKeyboardMarkup getInlineButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonSelect = new InlineKeyboardButton();
        buttonSelect.setText("Выбрать ответственную команду:");
        InlineKeyboardButton buttonCommand1 = new InlineKeyboardButton();
        buttonCommand1.setText("Support");
        InlineKeyboardButton buttonCommand2 = new InlineKeyboardButton();
        buttonCommand2.setText("WorkGroup");

        buttonSelect.setCallbackData("startState");
        buttonCommand1.setCallbackData("Support");
        buttonCommand2.setCallbackData("WorkGroup");


        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonSelect);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(buttonCommand1);

        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow3.add(buttonCommand2);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

}



