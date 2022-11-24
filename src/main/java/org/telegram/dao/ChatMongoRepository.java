package org.telegram.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.telegram.dao.model.Chat;
import org.telegram.dao.model.MailSubject;

import java.util.List;


@Repository
public interface ChatMongoRepository extends MongoRepository<Chat, String> {
    Chat findByChatId(long chatId);

    List<Chat> findByActivated(int activated);

    void deleteById(String id);
}
