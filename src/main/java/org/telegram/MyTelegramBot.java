package org.telegram;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.botapi.TelegramFacade;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
@Setter
@Getter
@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    private BotConfig botConfig;
    private  TelegramFacade telegramFacade;

    public MyTelegramBot(TelegramFacade telegramFacade, BotConfig botConfig) {
        this.telegramFacade = telegramFacade;
        this.botConfig = botConfig;
        try {
            // Register long polling bots. They work regardless type of TelegramBotsApi we are creating
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
        SendMessage replyMessageToUser = telegramFacade.handleUpdate(update);
        if (replyMessageToUser != null) {
            replyMessageToUser.enableHtml(true);
            try {
                execute(replyMessageToUser);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    public Message sendPhoto(long chatId, String imageCaption, String imagePath) {

        InputFile image = new InputFile(new File(imagePath));
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(image);
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption(imageCaption);
        sendPhoto.setParseMode("HTML");


        return execute(sendPhoto);
    }



    @SneakyThrows
    public void editMessageReplyMarkup(long chatId, int messageId, InlineKeyboardMarkup keyBoard) {
        EditMessageReplyMarkup sendMsg = new EditMessageReplyMarkup();
        sendMsg.setChatId(chatId);
        sendMsg.setReplyMarkup(keyBoard);
        sendMsg.setMessageId(messageId);
        execute(sendMsg);

    }

    @SneakyThrows
    public void sendMessage(long chatId, String text, InlineKeyboardMarkup keyBoard) {
        SendMessage sendMsg = new SendMessage();
        sendMsg.setText(text);
        sendMsg.setChatId(chatId);
        sendMsg.setReplyMarkup(keyBoard);
        sendMsg.setParseMode("HTML");
        execute(sendMsg);

    }
    @SneakyThrows
    public void sendMessage(long chatId, String text, boolean silent) {
        SendMessage sendMsg = new SendMessage();
        sendMsg.setText(text);
        sendMsg.setChatId(chatId);
        sendMsg.setParseMode("HTML");
        sendMsg.setDisableNotification(silent);
        execute(sendMsg);

    }


    public static InputMedia stringToInpFile(String path){
        InputMediaPhoto inputMediaPhoto = new InputMediaPhoto();
        inputMediaPhoto.setMedia(new File(path), path);
        return inputMediaPhoto;
    }
    @SneakyThrows
    public void sendMediaGroup(long chatId, List<String> listPath, String text) {
        System.out.println(listPath);
        SendMediaGroup sendMsg = new SendMediaGroup();
        sendMsg.setChatId(chatId);
        List<InputMedia> list = listPath.stream().map(MyTelegramBot::stringToInpFile).collect(Collectors.toList());
        list.get(0).setCaption(text);
        sendMsg.setMedias(list);
        sendMsg.setDisableNotification(true);
        execute(sendMsg);

    }



    @SneakyThrows
    public void sendDocument(long chatId, String path) {
        InputFile doc = new InputFile(new File(path));
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        sendDocument.setDocument(doc);
        sendDocument.setDisableNotification(true);
        execute(sendDocument);
    }
    @SneakyThrows
    public Message sendAudio(long chatId, String caption, InputFile sendFile) {
        SendAudio sendAudio = new SendAudio();
        sendAudio.setChatId(chatId);
        sendAudio.setCaption(caption);
        sendAudio.setAudio(sendFile);
        Message outputMsg = execute(sendAudio);
        return outputMsg;
    }

    @SneakyThrows
    public Message sendAudio(long chatId, String caption, InputFile sendFile, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendAudio sendAudio = new SendAudio();
        sendAudio.setChatId(chatId);
        sendAudio.setCaption(caption);
        sendAudio.setAudio(sendFile);
        sendAudio.setReplyMarkup(inlineKeyboardMarkup);
        Message outputMsg = execute(sendAudio);
        return outputMsg;
    }



    public void delMessage (Message message) throws TelegramApiException {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(message.getChatId());
        deleteMessage.setMessageId(message.getMessageId());
        execute(deleteMessage);

    }

}
