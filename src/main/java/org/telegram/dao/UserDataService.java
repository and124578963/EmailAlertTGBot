package org.telegram.dao;

import org.springframework.stereotype.Service;
import org.telegram.dao.model.Chat;
import org.telegram.dao.model.User;

import java.util.List;

/**
 * Сохраняет, удаляет, ищет анкеты пользователя.
 *
 * @author Sergei Viacheslaev
 */
@Service
public class UserDataService {

    private UserMongoRepository userMongoRepository;


    public UserDataService(UserMongoRepository userMongoRepository) {
        this.userMongoRepository = userMongoRepository;
    }

    public void saveUser(User user) {
        userMongoRepository.save(user);
    }

    public void createUser(String command, String name) {
        User user = new User();
        user.setCommand(command);
        user.setName(name);
        saveUser(user);
    }

    public List<User> getUsersOfCommand(String command) {
        return userMongoRepository.findByCommand(command);
    }

    public User getUserById(String id) {
        return userMongoRepository.findByUserId(id);
    }


    public void deleteUser(String name) {
        userMongoRepository.deleteByName(name);
    }
}
