package org.tgbot.dao.service;

import lombok.Setter;
import org.springframework.stereotype.Service;
import org.tgbot.dao.model.Chat;
import org.tgbot.dao.model.ChatUser;
import org.tgbot.dao.repository.UserMongoRepository;

import java.util.Optional;


@Setter
@Service
public class UserDataService {

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

    public ChatUser getUserById(String id) {
        Optional<ChatUser> ocu = userMongoRepository.findById(id);
        return ocu.orElseThrow(NullPointerException::new);
    }

    public void delete(ChatUser user) {
        userMongoRepository.delete(user);
    }
}
