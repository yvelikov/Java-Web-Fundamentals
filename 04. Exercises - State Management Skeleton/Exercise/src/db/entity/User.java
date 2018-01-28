package db.entity;

public class User {

    private String id;

    private String name;

    private String password;

    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public String toString() {
        return this.id + "|" + this.name + "|" + this.password;
    }
}
