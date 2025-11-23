import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private String quizID;
    private List<QuizQuestion> questions;

    public Quiz(String quizID) {
        this.quizID = quizID;
        this.questions = new ArrayList<>();
    }

    public void addQuestion(QuizQuestion question) {
        questions.add(question);
    }

    public String getQuizID() {
        return quizID;
    }

    public List<QuizQuestion> getQuestions() {
        return questions;
    }

    public int grade(List<Integer> StudentAnswers) {
        int score = 0;

        for (int i = 0; i < questions.size(); i++) {
            QuizQuestion question = questions.get(i);
            if (StudentAnswers.get(i) == question.getCorrectAnswer()) {
                score++;
            }
        }
        return score;
    }

}
