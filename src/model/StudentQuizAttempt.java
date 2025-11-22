    package model;

    public class StudentQuizAttempt {
        private double score;
        private boolean passed;
        private int attemptCount;

        public StudentQuizAttempt(){}
        public StudentQuizAttempt( double score, boolean passed){
            this.score=score;
            this.passed=passed;
            this.attemptCount=1;
        }
        public StudentQuizAttempt( double score, boolean passed, int attemptCount){
            this.score=score;
            this.passed=passed;
            this.attemptCount = attemptCount;
        }
        public double getScore(){return score;}
        public boolean isPassed(){return passed;}

        public void setPassed(boolean passed) {
            this.passed = passed;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public int getAttemptCount() {
            return attemptCount;
        }

        public void setAttemptCount(int attemptCount) {
            this.attemptCount = attemptCount;
        }
    }

