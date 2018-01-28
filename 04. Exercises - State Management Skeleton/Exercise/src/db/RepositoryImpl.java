package db;

import db.entity.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RepositoryImpl implements Repository {

    private static final String DB_PATH = System.getProperty("user.dir") + "\\src\\resources\\db\\users.txt";

    @Override
    public void persist(String userData) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(DB_PATH, true));
        writer.append(userData).append(System.lineSeparator());
        writer.flush();
        writer.close();
    }

    @Override
    public User findById(String id) throws IOException {
        return this.find(id, 0);
    }

    @Override
    public User findByEmail(String email) throws IOException {
        return this.find(email, 1);
    }

    @Override
    public List<User> findAll() throws IOException{
        List<User> registeredUsers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DB_PATH))) {
            while (true) {
                String line = reader.readLine();

                if (line == null) {
                    break;
                }

                String[] userData = line.split("\\|");
                User user = new User(userData[0], userData[1], userData[2]);
                registeredUsers.add(user);
            }
        }

        return registeredUsers;
    }

    private User find(String value, int index) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(DB_PATH))) {
            while (true) {
                String line = reader.readLine();

                if (line == null) {
                    break;
                }

                String[] userData = line.split("\\|");
                if (userData[index].equals(value)) {
                    return new User(userData[0], userData[1], userData[2]);
                }
            }
        }

        return null;
    }
}
