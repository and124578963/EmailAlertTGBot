package org.tgbot.dao.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.tgbot.dao.model.MailSubject;


import java.util.List;


@Repository
public interface MailSubjectsMongoRepository extends MongoRepository<MailSubject, String> {
    List<MailSubject> findByIsSent(boolean isSent);

}
