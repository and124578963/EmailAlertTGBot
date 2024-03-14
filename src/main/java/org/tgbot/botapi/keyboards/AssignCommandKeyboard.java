package org.tgbot.botapi.keyboards;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.tgbot.dao.model.ChatUser;

import java.util.ArrayList;
import java.util.List;

public class AssignCommandKeyboard extends  InlineKeyboardMarkup{

    public AssignCommandKeyboard(List<String> commands) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        InlineKeyboardButton buttonSelect1 = new InlineKeyboardButton();
        buttonSelect1.setText("Выбрать ответственную команду:");
        buttonSelect1.setCallbackData("startState");
        keyboardButtonsRow1.add(buttonSelect1);
        rowList.add(keyboardButtonsRow1);

        for (String commandName: commands){
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            InlineKeyboardButton buttonCommand1 = new InlineKeyboardButton();
            buttonCommand1.setText(commandName);
            buttonCommand1.setCallbackData(commandName);
            keyboardButtonsRow.add(buttonCommand1);
            rowList.add(keyboardButtonsRow);
        }

        setKeyboard(rowList);

    }

}
