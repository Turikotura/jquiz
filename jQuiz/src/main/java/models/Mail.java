package models;

public class Mail {
    private int id;
    private User sender;
    private User receiver;
    private MailTypes type;
    private String quiz;
    private String text;
    public Mail(int id, User sender, User receiver, MailTypes type, String quiz, String text){
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.quiz = quiz;
        this.text = text;
    }
    public int getId() { return id; }
    public User getSender() { return sender; }
    public User getReceiver() { return receiver; }
    public MailTypes getType() { return type; }
    public String getQuiz() { return quiz; }
    public String getText() { return text; }
}
