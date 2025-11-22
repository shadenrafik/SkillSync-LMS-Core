package model;

import java.util.List;

public class Quiz {
    private List<Question> questions;
    private int passScore;
    private int maxAttempts;
    public Quiz() {  }
    public Quiz(List<Question> questions, int passScore, int maxAttempts) {
        this.questions = questions;
        this.passScore = passScore;
        this.maxAttempts = maxAttempts;

    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public int getPassScore() {
        return passScore;
    }

    public void setPassScore(int passScore) {
        this.passScore = passScore;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}