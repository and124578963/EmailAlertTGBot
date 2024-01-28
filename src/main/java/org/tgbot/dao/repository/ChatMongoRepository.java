package org.tgbot.dao.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.tgbot.dao.model.Chat;

import java.util.List;
import java.util.Optional;


@Repository
public interface ChatMongoRepository extends MongoRepository<Chat, String> {

    List<Chat> findByActivated(boolean activated);
    Optional<Chat> findByTgChatId(long tgChatId);
}
