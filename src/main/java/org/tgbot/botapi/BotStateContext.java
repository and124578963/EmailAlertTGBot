package org.tgbot.botapi;

import org.springframework.stereotype.Component;
import org.tgbot.botapi.handlers.InputMessageHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Defines message handlers for each state.
 */
@Component
public class BotStateContext {
    private final Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    public Optional<SendMessage> processInputMessage(BotState currentState, Message message) {
        Optional<InputMessageHandler> currentMessageHandler = findMessageHandler(currentState);
        SendMessage reply = null;
        if (currentMessageHandler.isPresent()) {
            reply = currentMessageHandler.get().handle(message, currentState);
        }
        return Optional.ofNullable(reply);
    }

    private Optional<InputMessageHandler> findMessageHandler(BotState currentState) {
        System.out.println(currentState);
        return Optional.ofNullable(messageHandlers.get(switchState(currentState)));
    }

    private BotState switchState(BotState currentState) {
        switch (currentState) {
            case DEACTIVATE:
            case ACTIVATE:
                return BotState.ACTIVATION;

            case ADD_SUBS:
            case DELETE_SUBS:
            case ADDING_SUBS:
            case DELETING_SUBS:
            case LIST_SUBS:
                return BotState.SUBSCRIPTION;

            case ADD_USER:
            case ADDING_USER:
            case DELETE_USER:
            case DELETING_USER:
                return BotState.USERS;

            default:
                return currentState;
        }
    }


}





