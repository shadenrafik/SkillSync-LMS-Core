package model;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private String id;
    private String question;
    private List<String> answers;
    private int  correctAnswer;
    public Question() {
        this.answers = new ArrayList<>();
    }
    public Question(String question, List<String> answers, int correctAnswer,String id) {
        this.id=id;
        this.question = question;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
