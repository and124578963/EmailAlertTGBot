package org.telegram.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.MyTelegramBot;
import org.telegram.dao.ChatDataService;
import org.telegram.dao.MailSubjectsDataService;
import org.telegram.dao.model.Chat;
import org.telegram.dao.model.MailSubject;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@EnableScheduling
@Service
public class AlertService {
    private MyTelegramBot myTelegramBot;
    private MailSubjectsDataService mailSubjectsDataService;
    private ChatDataService chatDataService;

    @Autowired
    public AlertService(MyTelegramBot myTelegramBot, MailSubjectsDataService mailSubjectsDataService,
                        ChatDataService chatDataService) {
        this.myTelegramBot = myTelegramBot;
        this.mailSubjectsDataService = mailSubjectsDataService;
        this.chatDataService = chatDataService;
    }
    @SneakyThrows
    public static String checkImagePath(String path){
        System.out.println(path);
        if (path != null && (path.toLowerCase().contains(".png".toLowerCase()) ||  path.toLowerCase().contains(".jpg".toLowerCase()))){
            BufferedImage bimg = ImageIO.read(new File(path));
            int width          = bimg.getWidth();
            int height         = bimg.getHeight();
            if (((width / height) < 20) && ((height / width) < 20)){
                return path;
            } else {
                return "resources/badImage.png";
            }
        } else {
            return null;
        }
    }

    @SneakyThrows
    public static String checkLogPath(String path){
        System.out.println(path);
        if (path != null && (path.toLowerCase().contains(".log".toLowerCase()))){
            File file = new File(path);
            if (file.length()/(1024*1024)<20) {
                return path;
            }
        }
        return null;
    }

    @SneakyThrows
    @Scheduled(fixedDelay = 120000)
    private void eventService() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "python", "pythonMailModule/gmail_utils.py");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            StringBuilder textBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (process.getInputStream(), StandardCharsets.UTF_8))) {
                int c;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            }
            System.out.println(textBuilder);

            List<MailSubject> mailList = mailSubjectsDataService.getMailsNoSended();

            List<Chat> listChat = chatDataService.getActivatedChats();
            for (MailSubject i : mailList) {
                System.out.println(i.getKey());
                for (Chat chat : listChat) {

//                myTelegramBot.sendMessage(chat.getChatId(), String.valueOf(Emojis.SEPARATOR)+String.valueOf(Emojis.SEPARATOR)+String.valueOf(Emojis.SEPARATOR)+String.valueOf(Emojis.SEPARATOR)+String.valueOf(Emojis.SEPARATOR)+String.valueOf(Emojis.SEPARATOR)+String.valueOf(Emojis.SEPARATOR)+String.valueOf(Emojis.SEPARATOR)+String.valueOf(Emojis.SEPARATOR)+String.valueOf(Emojis.SEPARATOR), true);

                    List<String> listPaths = i.getAttachments().stream().map(AlertService::checkImagePath).filter(Objects::nonNull).collect(Collectors.toList());
                    if (listPaths.size() > 1) {
                        myTelegramBot.sendMediaGroup(chat.getChatId(), listPaths, i.getKey());
                    } else if (listPaths.size() == 1) {
                        myTelegramBot.sendPhoto(chat.getChatId(), i.getKey(), listPaths.get(0));
                    }
                    String text = i.getText();
                    int maxLength = Math.min(text.length(), 3800);
                    text = text.substring(0, maxLength);
                    myTelegramBot.sendMessage(chat.getChatId(), "<b>" + i.getKey() + ":</b> " + i.getSubject() + "\n\n" + text, getInlineButtons());
                    List<String> listLogPaths = i.getAttachments().stream().map(AlertService::checkLogPath).filter(Objects::nonNull).collect(Collectors.toList());
                    for (String p : listLogPaths) {
                        myTelegramBot.sendDocument(chat.getChatId(), p);
                    }
                }
                mailSubjectsDataService.setSended(i);
            }
        }catch (Exception e){
            List<Chat> listChat = chatDataService.getActivatedChats();
                for (Chat chat : listChat) {
                    String text = e.getMessage();
                    int maxLength = Math.min(text.length(), 3800);
                    text = text.substring(0, maxLength);
                    myTelegramBot.sendMessage(chat.getChatId(), text, false );

                }

        }

    }

    private InlineKeyboardMarkup getInlineButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonSelect = new InlineKeyboardButton();
        buttonSelect.setText("Выбрать ответственную команду:");
        InlineKeyboardButton buttonCommand1 = new InlineKeyboardButton();
        buttonCommand1.setText("Support");
        InlineKeyboardButton buttonCommand2 = new InlineKeyboardButton();
        buttonCommand2.setText("WorkGroup");

        buttonSelect.setCallbackData("None");
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
