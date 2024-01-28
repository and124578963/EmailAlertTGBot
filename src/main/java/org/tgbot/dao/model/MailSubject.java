package org.tgbot.dao.model;

import com.mongodb.lang.Nullable;
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
public class MailSubject implements Serializable {

    @Id
    String id;
    String topic;
    String subject;
    String text;

    @Field
    List<String> attachments = new ArrayList<>();

    boolean isSent;
    boolean enabledAssign;

    @Nullable
    String issueCode;

}
