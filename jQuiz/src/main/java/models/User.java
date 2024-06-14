package models;

import java.util.AbstractCollection;
import java.util.Date;
import java.util.List;

public class User {
    private int id;
    private String username;
    private boolean isAdmin;
    private Date created_at;
    private String email;
    private String password;
    private String image;
    public User(int id, String username, Date created_at, String email, String password, String image){
        this.id = id;
        this.username = username;
        this.created_at = created_at;
        this.email = email;
        this.password = password;
        this.image = image;
    }
    public int getId() { return id; }
    public String getUsername() { return username; }
    public boolean isAdmin() { return isAdmin; }
    public Date getCreated_at() { return created_at; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getImage() { return image; }
}