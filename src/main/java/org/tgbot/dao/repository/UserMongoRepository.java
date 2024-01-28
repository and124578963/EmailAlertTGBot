package org.tgbot.dao.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.tgbot.dao.model.ChatUser;

import java.util.List;


@Repository
public interface UserMongoRepository extends MongoRepository<ChatUser, String> {
    ChatUser findByName(String name);
    List<ChatUser> findByCommand(String command);

}
