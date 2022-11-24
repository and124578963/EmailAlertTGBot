package org.telegram.dao;

import org.springframework.stereotype.Service;
import org.telegram.dao.model.Chat;
import org.telegram.dao.model.MailSubject;

import java.util.List;

/**
 * Сохраняет, удаляет, ищет анкеты пользователя.
 *
 * @author Sergei Viacheslaev
 */
@Service
public class ChatDataService {

    private ChatMongoRepository chatMongoRepository;


    public ChatDataService(ChatMongoRepository chatMongoRepository) {
        this.chatMongoRepository = chatMongoRepository;
    }

    public void saveChat(Chat chat) {
        chatMongoRepository.save(chat);
    }

    public Chat getChat(long chatId) {
        Chat chat = chatMongoRepository.findByChatId(chatId);
        if (chat == null){
            chat = new Chat();
            chat.setChatId(chatId);
            chat.setActivated(0);
        }
        return chat;
    }

    public List<Chat> getActivatedChats() {
        return chatMongoRepository.findByActivated(1);
    }

    public void setActivated (Chat chat){
        chat.setActivated(1);
        saveChat(chat);

    }
    public void setDeactivated (Chat chat){
        chat.setActivated(0);
        saveChat(chat);

    }

}
