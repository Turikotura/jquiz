package models;

public class Answer {
    int id;
    private String text;
    private boolean isCorrect;
    private int questionId;
    private int uniquenessId;

    public Answer(int id, String text, boolean isCorrect, int questionId, int uniquenessId) {
        this.id = id;
        this.text = text;
        this.isCorrect = isCorrect;
        this.questionId = questionId;
        this.uniquenessId = uniquenessId;
    }

    public int getId() {return id;}
    public String getText() {return text;}
    public boolean getIsCorrect() {return this.isCorrect;}
    public int getQuestionId() {return questionId;}
    public int getUniquenessId() {return uniquenessId;}
    public boolean equals(Answer other) {return this.questionId == other.questionId && this.uniquenessId == other.getUniquenessId();}
}