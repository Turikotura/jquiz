package models;

import java.util.Date;

public class Mail {
    private int id;
    private int senderId;
    private int receiverId;
    private MailTypes type;
    private int quizId;
    private String text;
    private Date timeSent;
    public Mail(int id, int senderId, int receiverId, MailTypes type, int quizId, String text, Date timeSent){
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
        this.quizId = quizId;
        this.text = text;
        this.timeSent = timeSent;
    }
    public int getId() { return id; }
    public int getSenderId() { return senderId; }
    public int getReceiverId() { return receiverId; }
    public MailTypes getType() { return type; }
    public int getQuizId() { return quizId; }
    public String getText() { return text; }
    public Date getTimeSent() { return timeSent; }
}
