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
@Document(collection = "chat")
public class Chat implements Serializable {
    @Id
    String chId;
    long chatId;
    String userName;
    int activated;

    @Override
    public String toString() {
        return String.format("chId: %s%n" +
                             "chatId %d%n" +
                             "userName %s%n" +
                             "activated %d%n",
                              getChId(), getChatId(), getUserName(), getActivated());
    }
}
