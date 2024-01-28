package org.tgbot.dao.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document
public class ChatUser implements Serializable {
    @Id
    String id;

    String name;
    String command;

}
