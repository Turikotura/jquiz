package models;

public class Answer {
    private int id;
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
    @Override
    public boolean equals(Object other) {return this.questionId == ((Answer)other).getQuestionId() && this.uniquenessId == ((Answer)other).getUniquenessId();}
    @Override
    public int hashCode(){return this.questionId ^ this.uniquenessId;}
    public boolean equals(String other) {return this.text.equals(other);}

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
}