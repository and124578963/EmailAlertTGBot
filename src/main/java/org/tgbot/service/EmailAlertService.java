package org.tgbot.service;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.tgbot.BotConfig;
import org.tgbot.EmailAlertBot;
import org.tgbot.botapi.keyboards.AssignCommandKeyboard;
import org.tgbot.dao.service.ChatDataService;
import org.tgbot.dao.service.MailSubjectsDataService;
import org.tgbot.dao.model.Chat;
import org.tgbot.dao.model.MailSubject;
import org.tgbot.service.adapters.EmailPythonAdapter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Slf4j
@Setter
@EnableScheduling
@Service
public class EmailAlertService {
    @Autowired
    private EmailAlertBot emailAlertBot;
    @Autowired
    private MailSubjectsDataService mailSubjectsDataService;
    @Autowired
    private ChatDataService chatDataService;
    @Autowired
    private BotConfig botConfig;
    @Autowired
    private EmailPythonAdapter emailPythonAdapter;

    @Autowired
    private void notifyUsersBotIsUp() throws TelegramApiException {
        List<Chat> listChat = chatDataService.getActivatedChats();
        for (Chat chat : listChat){
            emailAlertBot.sendMessageSilently(chat.getTgChatId(), botConfig.getHelloMessage());
        }
    }

    private int attemptToSend = 1;

    @SneakyThrows
    @Scheduled(fixedDelay = 120000)
    private void eventService() {
        try {
            emailPythonAdapter.run();
            List<Chat> listChat = chatDataService.getActivatedChats();
            List<MailSubject> mailList = mailSubjectsDataService.getMailsNoSent();

            for (MailSubject mail : mailList) {
                log.info(String.format("Start processing email %s", mail.toString()));

                for (Chat chat : listChat) {
                    if (chat.getSubscribes().contains(mail.getFolder())) {
                        log.info(String.format("Sent to chat %s", chat));
                        sendImageAttachments(chat, mail);
                        sendTextMessage(chat, mail);
                        mailSubjectsDataService.setIsSent(mail);
                        sendFileAttachments(chat, mail);
                    } }

                for (String path: mail.getAttachments()){
                    new File (path).delete();
                }

            }
            attemptToSend = 1;

        }catch (Exception e){
            log.info("Attempt number {} failed.", attemptToSend);
            e.printStackTrace();
            if (attemptToSend++ > botConfig.getMaxAttemptsToSend()) {
                List<Chat> listChat = chatDataService.getActivatedChats();
                for (Chat chat : listChat) {
                    String text = e.getMessage();
                    int maxLength = Math.min(text.length(), 3800);
                    text = text.substring(0, maxLength);
                    emailAlertBot.sendMessageSilently(chat.getTgChatId(), text);
                    emailAlertBot.sendMessage(chat.getTgChatId(), botConfig.getGoodbyeMessage());
                }
                System.exit(1);
            }
        }

    }

    private void sendTextMessage(Chat chat, MailSubject mailSubject) throws TelegramApiException {
        String text = mailSubject.getBody();
        int maxLength = Math.min(text.length(), 3800);
        text = text.substring(0, maxLength);
        if (! mailSubject.isConverted_to_image()){
            String message = mailSubject.getSubject() + "\n\n" + text;
            if (mailSubject.isEnabledAssign()) {
                emailAlertBot.sendMessageWithKeyboard(chat.getTgChatId(), message , new AssignCommandKeyboard(botConfig.getAssignPulls()));
            } else {
                emailAlertBot.sendMessage(chat.getTgChatId(), message);
            }
        }

    }
    private void sendFileAttachments(Chat chat, MailSubject mailSubject) throws TelegramApiException {
        List<String> listLogPaths = mailSubject.getAttachments().stream()
                .map(EmailAlertService::verifyFileAttachment)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        for (String p : listLogPaths) {
            emailAlertBot.sendDocument(chat.getTgChatId(), p);
        }
    }
    private void sendImageAttachments(Chat chat, MailSubject mailSubject)
            throws TelegramApiException {

        List<String> listAttachmentsPath = mailSubject.getAttachments().stream()
                .map(EmailAlertService::verifyImageAttachment)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (listAttachmentsPath.size() > 1) {
            int maxIndex = Math.min(listAttachmentsPath.size(), 10);
            emailAlertBot.sendMediaGroup(chat.getTgChatId(), listAttachmentsPath.subList(0, maxIndex),
                    mailSubject.getSubject());
        } else if (listAttachmentsPath.size() == 1) {
            emailAlertBot.sendPhoto(chat.getTgChatId(), mailSubject.getSubject(), listAttachmentsPath.get(0));
        }
    }

    @SneakyThrows
    public static String verifyImageAttachment(String path) {
        System.out.println(path);
        if (path != null && (path.toLowerCase().contains(".png".toLowerCase()) || path.toLowerCase().contains(".jpg".toLowerCase()))) {
            File file = new File(path);
            BufferedImage bimg = ImageIO.read(file);
            int width = bimg.getWidth();
            int height = bimg.getHeight();
            if (((width / height) < 20) && ((height / width) < 20) && (file.length() / (1024 * 1024) < 10)) {
                return path;
            } else {
                return "classpath:badImage.png";
            }
        } else {
            return null;
        }
    }

    @SneakyThrows
    public static String verifyFileAttachment(String path) {
        System.out.println(path);
        if (path != null && (path.toLowerCase().contains(".log".toLowerCase()) ||
                path.toLowerCase().contains(".txt".toLowerCase()) ||
                path.toLowerCase().contains(".tar".toLowerCase()) ||
                path.toLowerCase().contains(".gz".toLowerCase())
        )) {
            File file = new File(path);
            if (file.length() / (1024 * 1024) < 20) {
                return path;
            }
        }
        return null;
    }
}