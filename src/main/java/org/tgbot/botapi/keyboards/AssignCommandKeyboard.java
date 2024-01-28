package org.tgbot.botapi.keyboards;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class AssignCommandKeyboard extends  InlineKeyboardMarkup{

    public AssignCommandKeyboard() {
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

        setKeyboard(rowList);

    }

}
