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
@Document(collection = "mails")
public class MailSubject implements Serializable {

    @Id
    String id;
    String date;
    String sender;
    String folder;
    String receiver;
    String subject;
    String body;

    @Field
    List<String> attachments = new ArrayList<>();

    @Field(name="is_sent")
    boolean isSent;

    @Field(name="enable_assigne")
    boolean enabledAssign;

    @Field(name="converted_to_image")
    boolean converted_to_image;

}
