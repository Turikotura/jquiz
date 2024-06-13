import java.util.HashSet;

public class ClosedQuestion extends Question {
    private int questionType;
    private String queryText;
    private int userScore;
    private HashSet<String> correctAnswers;
    private ArrayList<String> allAnswers;
    private boolean guessed;


    public ClosedQuestion(String text, ArrayList<String> answers, HashSet<String> correctAnswers, int type) {
        super(text,correctAnswers,type);
        this.correctAnswers = correctAnswersnswers;
    }

    public String getQueryText() {return queryText;}
    public int getScore() {return userScore;}
    public int type() {return questionType;}
    public ArrayList<String> getAnswers() {return allAnswers;}
    public boolean isMultiAnswer() {return isMulti;}
    public boolean guessAnswer(String ans) {super.guessAnswer(ans);}
}