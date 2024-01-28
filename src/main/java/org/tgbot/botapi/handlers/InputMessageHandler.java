package org.tgbot.botapi.handlers;

import org.tgbot.botapi.BotState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;



public interface InputMessageHandler {
    SendMessage handle(Message message, BotState botState);

    BotState getHandlerName();
}
