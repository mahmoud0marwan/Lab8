import java.util.List;

public class QuizQuestion {
    private String questionText;
    private List<String> choices;
    private int correctAnswer;

    public QuizQuestion(String questionText, List<String> choices, int correctAnswer) {
        this.questionText = questionText;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getChoices() {
        return choices;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }
}
