package org.telegram.dao.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Данные пришедших сообщений
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "users")
public class User implements Serializable {
    @Id
    String userId;
    String name;
    String command;

    @Override
    public String toString() {
        return String.format("userId: %s%n" +
                             "name %s%n" +
                             "command %s%n",
                              getUserId(), getName(), getCommand());
    }
}
