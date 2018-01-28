package db;

import db.entity.User;

import java.io.IOException;
import java.util.List;

public interface Repository {

    void persist(String userData) throws IOException;

    User findById(String id) throws IOException;

    User findByEmail(String email) throws IOException;

    List<User> findAll() throws IOException;
}
