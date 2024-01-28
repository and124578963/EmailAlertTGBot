package org.tgbot.dao.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document
public class Chat implements Serializable {

    @Id
    String id;

    long tgChatId;
    @Field
    boolean activated = false;
    @Field
    List<String> subscribes = new ArrayList<>();
    @Field
    List<ChatUser> chatUsers = new ArrayList<>();

}
