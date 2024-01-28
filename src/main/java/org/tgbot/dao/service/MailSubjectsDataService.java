package org.tgbot.dao.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tgbot.dao.model.MailSubject;
import org.tgbot.dao.repository.MailSubjectsMongoRepository;


import java.util.*;

@Setter
@Service
public class MailSubjectsDataService {
    @Autowired
    private MailSubjectsMongoRepository mailSubjectsMongoRepository;


    public void saveMailSubject(MailSubject mailSubject) {
        mailSubjectsMongoRepository.save(mailSubject);
    }

    public List<MailSubject> getMailsNoSent() {
        return mailSubjectsMongoRepository.findByIsSent(false);
    }

    public void deleteMail(MailSubject mail) {
        mailSubjectsMongoRepository.delete(mail);
    }

    public void setIsSent(MailSubject mailSubject){
        mailSubject.setSent(true);
        saveMailSubject(mailSubject);
    }

}
