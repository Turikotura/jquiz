import java.util.HashSet;

public class ClosedQuestion extends Question {
    private int questionType;
    private String queryText;
    private int userScore;
    private Set<String> correctAnswers;
    private List<String> allAnswers;
    private boolean guessed;


    public ClosedQuestion(String text, List<String> answers, Set<String> correctAnswers, int type) {
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