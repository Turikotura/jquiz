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
    private List<User> friends;
    private List<Mail> mails;
    private List<Achievements> achievements;
    public User(int id, String username, Date created_at, String email, String password, String image,
                List<User> friends, List<Mail> mails, List<Achievements> achievements){
        this.id = id;
        this.username = username;
        this.created_at = created_at;
        this.email = email;
        this.password = password;
        this.image = image;

        this.friends = friends;
        this.mails = mails;
        this.achievements = achievements;
    }
    public int getId() { return id; }
    public String getUsername() { return username; }
    public boolean isAdmin() { return isAdmin; }
    public Date getCreated_at() { return created_at; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getImage() { return image; }

    public List<User> getFriends() { return friends; }
    public List<Mail> getMails() { return mails; }
    public List<Achievements> getAchievements() { return achievements; }
}