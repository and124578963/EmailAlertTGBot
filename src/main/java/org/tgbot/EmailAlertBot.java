package org.tgbot;
;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.*;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.media.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.tgbot.botapi.TelegramFacade;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;


import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EmailAlertBot extends TelegramLongPollingBot {
    @Autowired
    private TelegramFacade telegramFacade;
    @Autowired
    private BotConfig botConfig;

    @Autowired
    private void activateGettingMessageFromUsers(){
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            log.error("Error while sending to TG api", e);
        }
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotUserName();
    }


    @Override
    public void onUpdateReceived(Update update) {
        try {
            Optional<SendMessage> optionalReply = telegramFacade.handleUpdate(update);
            if (optionalReply.isPresent()) {
                SendMessage reply = optionalReply.get();
                reply.enableHtml(true);
                execute(reply);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();

        }

    }

    public void sendPhoto(long chatId, String imageCaption, String imagePath)
            throws TelegramApiException {
        InputFile image = new InputFile(new File(imagePath));
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(image);
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption(imageCaption);
        execute(sendPhoto);
    }

    public void editMessageReplyMarkup(long chatId, int messageId, InlineKeyboardMarkup keyBoard)
            throws TelegramApiException {
        EditMessageReplyMarkup sendMsg = new EditMessageReplyMarkup();
        sendMsg.setChatId(chatId);
        sendMsg.setReplyMarkup(keyBoard);
        sendMsg.setMessageId(messageId);
        execute(sendMsg);
    }

    public void sendMessageWithKeyboard(long chatId, String text, InlineKeyboardMarkup keyBoard)
            throws TelegramApiException {
        SendMessage sendMsg = new SendMessage();
        text = text.strip();
        text = text.length() > 4 ? text.trim() : "Empty body";
        sendMsg.setText(text);
        sendMsg.setChatId(chatId);
        sendMsg.setReplyMarkup(keyBoard);
        execute(sendMsg);
    }

    public void sendMessageSilently(long chatId, String text) throws TelegramApiException {
        SendMessage sendMsg = new SendMessage();
        text = text.strip();
        text = text.length() > 4 ? text.trim() : "Empty body";
        sendMsg.setText(text);
        sendMsg.setChatId(chatId);
        sendMsg.setDisableNotification(true);
        execute(sendMsg);
    }

    public void sendReplyMessage(long chatId, String text, int messageId) throws TelegramApiException {
        SendMessage sendMsg = new SendMessage();
        text = text.strip();
        text = text.length() > 4 ? text.trim() : "Empty body";
        sendMsg.setText(text);
        sendMsg.setChatId(chatId);
        sendMsg.setReplyToMessageId(messageId);
        execute(sendMsg);
    }

    public void sendMessage(long chatId, String text) throws TelegramApiException {
        SendMessage sendMsg = new SendMessage();
        text = text.strip();
        text = text.length() > 4 ? text.trim() : "Empty body";
        sendMsg.setText(text);
        sendMsg.setChatId(chatId);
        execute(sendMsg);
    }


    public void sendMediaGroup(long chatId, List<String> listPath, String text)
            throws TelegramApiException {
        System.out.println(listPath);
        SendMediaGroup sendMsg = new SendMediaGroup();
        sendMsg.setChatId(chatId);
        List<InputMedia> list = listPath.stream().map(this::stringToInpFile).collect(Collectors.toList());
        list.get(0).setCaption(text);
        sendMsg.setMedias(list);
        sendMsg.setDisableNotification(true);
        execute(sendMsg);
    }

    private InputMedia stringToInpFile(String path){
        InputMediaPhoto inputMediaPhoto = new InputMediaPhoto();
        inputMediaPhoto.setMedia(new File(path), path);
        return inputMediaPhoto;
    }

    public void sendDocument(long chatId, String path) throws TelegramApiException {
        InputFile doc = new InputFile(new File(path));
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        sendDocument.setDocument(doc);
        sendDocument.setDisableNotification(true);
        execute(sendDocument);
    }


}
