    package model;

    public class StudentQuizAttempt {
        private double score;
        private boolean passed;

        public StudentQuizAttempt(){}
        public StudentQuizAttempt( double score, boolean passed){
            this.score=score;
            this.passed=passed;
        }
        public double getScore(){return score;}
        public boolean isPassed(){return passed;}

        public void setPassed(boolean passed) {
            this.passed = passed;
        }

        public void setScore(double score) {
            this.score = score;
        }

    }

