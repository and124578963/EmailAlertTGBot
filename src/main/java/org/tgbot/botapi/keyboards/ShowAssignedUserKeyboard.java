package org.tgbot.botapi.keyboards;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.tgbot.dao.model.ChatUser;

import java.util.ArrayList;
import java.util.List;

public class ShowAssignedUserKeyboard extends InlineKeyboardMarkup{

    public ShowAssignedUserKeyboard(ChatUser user){
        InlineKeyboardButton userButton = new InlineKeyboardButton();
        userButton.setText(user.getName());
        userButton.setCallbackData(user.getCommand());

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(userButton);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);
        setKeyboard(rowList);
    }

}

