package org.tgbot.dao.service;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tgbot.dao.model.Chat;
import org.tgbot.dao.model.ChatUser;
import org.tgbot.dao.repository.UserMongoRepository;

import java.util.Optional;


@Setter
@Service
public class UserDataService {
    @Autowired
    private UserMongoRepository userMongoRepository;


    public void saveUser(ChatUser user) {
        userMongoRepository.save(user);
    }

    public ChatUser createUser(String command, String name) {
        ChatUser user = new ChatUser();
        user.setCommand(command);
        user.setName(name);
        saveUser(user);
        return user;
    }

    public Optional<ChatUser> getUserById(String id) {
        Optional<ChatUser> ocu = userMongoRepository.findById(id);
        return ocu;
    }

    public void delete(ChatUser user) {
        userMongoRepository.delete(user);
    }
}
