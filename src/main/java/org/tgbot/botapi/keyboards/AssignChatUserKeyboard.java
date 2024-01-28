package org.tgbot.botapi.keyboards;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.tgbot.dao.model.ChatUser;
import org.tgbot.dao.service.ChatDataService;
import org.tgbot.utils.Emojis;

import java.util.ArrayList;
import java.util.List;

public class AssignChatUserKeyboard extends InlineKeyboardMarkup {

    public AssignChatUserKeyboard(String command, List<ChatUser> chatUsers){
        InlineKeyboardButton buttonSelectedCommand = new InlineKeyboardButton();
        String backName = String.format("%s %s", Emojis.ARROW_BACK, command);
        buttonSelectedCommand.setText(backName);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonSelectedCommand);
        rowList.add(keyboardButtonsRow1);
        chatUsers.removeIf(u -> !u.getCommand().equalsIgnoreCase(command));

        for (ChatUser user: chatUsers){
            System.out.println(user.getId());
            InlineKeyboardButton buttonUser = new InlineKeyboardButton();
            buttonUser.setText(user.getName());
            buttonUser.setCallbackData(user.getId());
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            keyboardButtonsRow.add(buttonUser);
            rowList.add(keyboardButtonsRow);
        }

        setKeyboard(rowList);

    }

}
