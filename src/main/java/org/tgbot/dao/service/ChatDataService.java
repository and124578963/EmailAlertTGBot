package org.tgbot.dao.service;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tgbot.dao.model.Chat;
import org.tgbot.dao.model.ChatUser;
import org.tgbot.dao.repository.ChatMongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Setter
@Service
public class ChatDataService {
    @Autowired
    private ChatMongoRepository chatMongoRepository;
    @Autowired
    private UserDataService userDataService;


    public void saveChat(Chat chat) {
        chatMongoRepository.save(chat);
    }

    public Chat findChatOrNew(long id) {
        Optional<Chat> opt_chat = chatMongoRepository.findByTgChatId(id);
        return opt_chat.orElseGet(() -> {
            Chat ch = new Chat();
            ch.setTgChatId(id);
            return ch;
        });
    }

    public List<Chat> getActivatedChats() {
        return chatMongoRepository.findByActivated(true);
    }

    public void setActivated(Chat chat){
        chat.setActivated(true);
        saveChat(chat);
    }
    public void setDeactivated(Chat chat){
        chat.setActivated(false);
        saveChat(chat);
    }

    public void addSubscribe(long chatId, String topic){
        Chat chat = findChatOrNew(chatId);
        chat.getSubscribes().add(topic);
        saveChat(chat);
    }
    public boolean delSubscribe(long chatId, String topic){
        Chat chat = findChatOrNew(chatId);
        boolean isDeleted = chat.getSubscribes().removeIf(topic::equalsIgnoreCase);
        saveChat(chat);
        return isDeleted;
    }

    public void addChatUser(long chatId, String command, String name){
        ChatUser user = userDataService.createUser(command, name);
        Chat chat = findChatOrNew(chatId);
        chat.getChatUsers().add(user);
        saveChat(chat);
    }

    public boolean delChatUser(long chatId, String name){
        Chat chat = findChatOrNew(chatId);
        List<ChatUser> users = chat.getChatUsers();
        users.forEach(u ->{
            if (u.getName().equalsIgnoreCase(name)){
                userDataService.delete(u);
            }
        });
        boolean isGood = users.removeIf(x -> x.getName().equalsIgnoreCase(name));
        chat.setChatUsers(users);
        return isGood;
    }

    public List<ChatUser> getChatUserByCommand(long chatId, String command){
        Chat chat = findChatOrNew(chatId);
        List<ChatUser> users = chat.getChatUsers();

        return users.stream().filter(user -> user.getCommand().equalsIgnoreCase(command)).collect(Collectors.toList());

    }

}



