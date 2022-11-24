package org.telegram.dao;

import org.springframework.stereotype.Service;
import org.telegram.dao.model.MailSubject;



import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Сохраняет, удаляет, ищет анкеты пользователя.
 *
 * @author Sergei Viacheslaev
 */
@Service
public class MailSubjectsDataService {

    private MailSubjectsMongoRepository mailSubjectsMongoRepository;


    public MailSubjectsDataService(MailSubjectsMongoRepository mailSubjectsMongoRepository) {
        this.mailSubjectsMongoRepository = mailSubjectsMongoRepository;
    }

    public void saveMailSubject(MailSubject mailSubject) {
        mailSubjectsMongoRepository.save(mailSubject);
    }

    public MailSubject getMailsById(String mailId) {
        return mailSubjectsMongoRepository.findByMailId(mailId);
    }

    public List<MailSubject> getMailsNoSended() {
        return mailSubjectsMongoRepository.findBySended(0);
    }

    public void deleteMailById(String Id) {
        mailSubjectsMongoRepository.deleteById(Id);
    }


    public void setSended (MailSubject mailSubject){
        mailSubject.setSended(1);
        saveMailSubject(mailSubject);

    }


}
