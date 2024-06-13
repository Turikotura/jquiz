public class Answer {
    private String text;
    private boolean isCorrect;
    private int questionId,uniquenessId;

    public Answer(String text, boolean isCorrect, int questionId, int uniquenessId) {
        this.text = text;
        this.isCorrect = isCorrect;
        this.questionId = questionId;
        this.uniquenessId = uniquenessId;
    }

    public String getText() {return text;}
    public boolean isCorrect() {return this.isCorrect;}
    public int getQuestionId() {return questionId;}
    public int getUniquenessId() {return uniquenessId;}
    public boolean equals(Answer other) {return this.questionId == other.questionId && this.uniquenessId == other.getUniquenessId();}
}