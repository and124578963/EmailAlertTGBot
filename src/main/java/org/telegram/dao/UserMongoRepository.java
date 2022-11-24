package org.telegram.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.telegram.dao.model.Chat;
import org.telegram.dao.model.User;

import java.util.List;


@Repository
public interface UserMongoRepository extends MongoRepository<User, String> {
    User findByName(String name);

    User findByUserId(String name);

    List<User> findByCommand(String command);

    void deleteByName(String name);
}
