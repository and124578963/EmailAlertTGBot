package org.telegram.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.telegram.dao.model.MailSubject;



import java.util.List;


@Repository
public interface MailSubjectsMongoRepository extends MongoRepository<MailSubject, String> {
    List<MailSubject> findBySended(int sended);

    MailSubject findByMailId(String id);

    void deleteById(String id);
}
