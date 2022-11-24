package org.telegram.dao.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * Данные пришедших сообщений
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "mail_subjects")
public class MailSubject implements Serializable {
    @Id
    String mailId;
    String key;
    String subject;
    String text;
    List<String> attachments;
    int sended;

    @Override
    public String toString() {
        return String.format("mailSubjectId: %s%n" +
                "key %s%n" +
                        "subject %s%n",
                getMailId(), getKey(), getSubject());
    }
}
