package gui;

import model.*;
import backend.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.Instant;

public class LessonViewerPanel extends JPanel implements ListSelectionListener, ActionListener {

    private StudentDashboard parentDashboard;
    private Course currentCourse;
    private JList<Lesson> lessonList;
    private DefaultListModel<Lesson> lessonListModel;
    private CardLayout contentCardLayout;
    private JPanel contentPanel;
    private JTextArea lessonContentArea;
    private JPanel quizViewPanel;
    private JButton mainActionButton;
    private JButton backButton;
    private JLabel progressLabel;
    private Student currentStudent;
    private CourseManager courseManager;
    private Map<Question, Integer> currentQuizAnswers;
    private Quiz currentQuiz;
    private Lesson currentLesson;
    private int currentAttemptNumber = 0;

    public LessonViewerPanel(StudentDashboard parent) {
        this.parentDashboard = parent;
        this.courseManager = new CourseManager("src/database/courses.json");
        setLayout(new BorderLayout(10, 10));


        JPanel topPanel = new JPanel(new BorderLayout());
        backButton = new JButton("<- Back to My Courses");
        backButton.addActionListener(this);

        progressLabel = new JLabel();
        progressLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(progressLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);


        lessonListModel = new DefaultListModel<>();
        lessonList = new JList<>(lessonListModel);
        lessonList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        lessonList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Lesson lesson) {
                    String title = lesson.getTitle();
                    if (currentStudent != null && currentStudent.hasPassedQuiz(currentCourse.getCourseId(), lesson.getLessonId())) {
                        title += " (Passed ✔️)";
                    } else if (currentStudent != null && currentStudent.getProgressForCourse(currentCourse.getCourseId()).contains(lesson.getLessonId())) {
                        title += " (Completed)";
                    }
                    setText(title);
                }
                return c;
            }

        });

        lessonList.addListSelectionListener(this);

        JScrollPane lessonListScrollPane = new JScrollPane(lessonList);
        lessonListScrollPane.setPreferredSize(new Dimension(250, 0));
        add(lessonListScrollPane, BorderLayout.WEST);

        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);

        lessonContentArea = new JTextArea("Select a lesson to view its content.");
        lessonContentArea.setEditable(false);
        lessonContentArea.setLineWrap(true);
        lessonContentArea.setWrapStyleWord(true);
        lessonContentArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        quizViewPanel = new JPanel();
        quizViewPanel.setLayout(new BoxLayout(quizViewPanel, BoxLayout.Y_AXIS));

        contentPanel.add(new JScrollPane(lessonContentArea), "LessonContent");
        contentPanel.add(new JScrollPane(quizViewPanel), "QuizView");
        add(contentPanel, BorderLayout.CENTER);

        mainActionButton = new JButton("Action Button");
        mainActionButton.addActionListener(this);
        add(mainActionButton, BorderLayout.SOUTH);
    }


    public void loadCourse(Student student, Course course) {
        this.currentStudent = student;
        this.currentCourse = course;


        List<Lesson> lessons = currentCourse.getLessons();
        lessonListModel.clear();
        lessons.forEach(lessonListModel::addElement);


        lessonContentArea.setText("Select a lesson to view its content.");
        contentCardLayout.show(contentPanel, "LessonContent");
        mainActionButton.setText("Select a Lesson");
        mainActionButton.setEnabled(false);

        updateProgress();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            Lesson selectedLesson = lessonList.getSelectedValue();
            currentLesson = selectedLesson;
            if (selectedLesson == null) {
                currentAttemptNumber = 0;
                lessonContentArea.setText("Select a lesson to view its content.");
                mainActionButton.setText("Select a Lesson");
                mainActionButton.setEnabled(false);
                return;
            }
            int selectedIndex = lessonListModel.indexOf(selectedLesson);
            if (selectedIndex > 0) {
                Lesson previousLesson = lessonListModel.getElementAt(selectedIndex - 1);
                String prevLessonId = previousLesson.getLessonId();

                if (!currentStudent.getProgressForCourse(currentCourse.getCourseId()).contains(prevLessonId)) {
                    JOptionPane.showMessageDialog(this, "You must complete the previous lesson: '" + previousLesson.getTitle() + "' before accessing this lesson.", "Lesson Blocked", JOptionPane.WARNING_MESSAGE);
                    lessonList.clearSelection();
                    return;
                }
            }

            contentCardLayout.show(contentPanel, "LessonContent");
            lessonContentArea.setText(selectedLesson.getContent());

            Quiz quiz = selectedLesson.getQuiz();

            if (quiz == null) {
                mainActionButton.setText("Error: Quiz Missing");
                mainActionButton.setEnabled(false);
                JOptionPane.showMessageDialog(this, "Configuration Error: Lesson is missing a quiz.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            currentAttemptNumber = 0;
            if (currentStudent.getQuizAttempts().containsKey(currentCourse.getCourseId())) {
                StudentQuizAttempt lastAttempt = currentStudent.getQuizAttempts().get(currentCourse.getCourseId()).get(selectedLesson.getLessonId());
                if (lastAttempt != null) {
                    currentAttemptNumber = lastAttempt.getAttemptCount();
                }
            }

            if (currentStudent.hasPassedQuiz(currentCourse.getCourseId(), selectedLesson.getLessonId())) {
                mainActionButton.setText("Quiz Passed!");
                mainActionButton.setEnabled(false);
            } else if (currentAttemptNumber >= quiz.getMaxAttempts()) {
                mainActionButton.setText("Failed - Max Attempts Reached (" + quiz.getMaxAttempts() + ")");
                mainActionButton.setEnabled(false);
            } else {
                int attemptsRemaining = quiz.getMaxAttempts() - currentAttemptNumber;
                mainActionButton.setText("Start Quiz for " + quiz.getPassScore() + "% (Attempts Left: " + attemptsRemaining + ")");
                mainActionButton.setActionCommand("StartQuiz");
                mainActionButton.setEnabled(true);
            }}}

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (e.getSource() == backButton) {
            parentDashboard.showPanel("ViewEnrolledCourses");

        } else if (currentLesson == null) {
            JOptionPane.showMessageDialog(this, "Please select a lesson first.", "No Lesson Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        switch (command) {
            case "StartQuiz":
                currentQuiz = currentLesson.getQuiz();
                if (currentQuiz == null || currentQuiz.getQuestions().isEmpty()) return;

                loadQuizView(currentQuiz);
                contentCardLayout.show(contentPanel, "QuizView");
                mainActionButton.setText("Submit Quiz");
                mainActionButton.setActionCommand("SubmitQuiz");
                break;

            case "SubmitQuiz":
                processQuizSubmission(currentLesson, currentQuiz);

                contentCardLayout.show(contentPanel, "LessonContent");

                updateProgress();
                lessonList.repaint();
                break;
        }
    }

    private void loadQuizView(Quiz quiz) {
        quizViewPanel.removeAll();
        currentQuizAnswers = new HashMap<>();

        int preferredWidth = 700;
        int qNumber = 1; 

        for (Question q : quiz.getQuestions()) {

            JPanel qPanel = new JPanel();
            qPanel.setLayout(new BoxLayout(qPanel, BoxLayout.Y_AXIS));
            qPanel.setBorder(BorderFactory.createTitledBorder("Question " + qNumber));
            qPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextArea questionText = new JTextArea("Q" + qNumber + ": " + q.getQuestion());
            questionText.setLineWrap(true);
            questionText.setWrapStyleWord(true);
            questionText.setEditable(false);
            questionText.setOpaque(false);
            questionText.setMaximumSize(new Dimension(preferredWidth, Integer.MAX_VALUE));

            qPanel.add(questionText);

            // Answers
            ButtonGroup group = new ButtonGroup();
            for (int i = 0; i < q.getAnswers().size(); i++) {

                String answer = q.getAnswers().get(i);
                JRadioButton option = new JRadioButton(answer);
                option.setAlignmentX(Component.LEFT_ALIGNMENT);

                final int index = i;
                option.addActionListener(e -> currentQuizAnswers.put(q, index));

                option.setMaximumSize(new Dimension(preferredWidth, option.getPreferredSize().height * 2));
                group.add(option);
                qPanel.add(option);
            }

            quizViewPanel.add(qPanel);
            quizViewPanel.add(Box.createVerticalStrut(10));

            qNumber++; 
        }

        quizViewPanel.revalidate();
        quizViewPanel.repaint();
    }

    private void processQuizSubmission(Lesson lesson, Quiz quiz) {
        int correctCount = 0;
        int totalQuestions = quiz.getQuestions().size();
        StringBuilder feedback = new StringBuilder();

        for (int i = 0; i < totalQuestions; i++) {
            Question q = quiz.getQuestions().get(i);
            Integer selectedIndex = currentQuizAnswers.get(q);

            if (selectedIndex != null && selectedIndex.equals(q.getCorrectAnswer())) {
                correctCount++;
            } else {
                String correctText = q.getAnswers().get(q.getCorrectAnswer());
                feedback.append("\nQuestion ").append(i + 1).append(": Incorrect.");
                feedback.append("\n\tCorrect Answer: ").append(correctText);
            }
        }
        int newAttemptNumber = currentAttemptNumber + 1;
        int scorePercentage = (int) ((double) correctCount / totalQuestions * 100);
        boolean passed = scorePercentage >= quiz.getPassScore();

        StudentQuizAttempt attempt = new StudentQuizAttempt(
                scorePercentage,
                passed,newAttemptNumber);
        currentAttemptNumber = newAttemptNumber;
        courseManager.recordLessonQuizResult(
                currentStudent.getUserId(),
                currentCourse.getCourseId(),
                lesson.getLessonId(),
                attempt
        );


        JsonDatabaseManager db = new JsonDatabaseManager();
        List<User> allUsers = db.loadUsers();
        for (User u : allUsers) {
            if (u.getUserId().equals(currentStudent.getUserId()) && u instanceof Student) {
                this.currentStudent = (Student) u;
                break;
            }
        }
        lessonList.repaint();
        updateProgress();

        String message = "Quiz Submitted!Score: " + correctCount + " / " + totalQuestions + " (" + scorePercentage + "%)\n";
        if (passed) {
            message += "Status: PASSED! (Target: " + quiz.getPassScore() + "%)";
            JOptionPane.showMessageDialog(this, message, "Quiz Results", JOptionPane.INFORMATION_MESSAGE);

            mainActionButton.setEnabled(false);
            mainActionButton.setText("Quiz Passed!");

            CompletionManager completionManager=new CompletionManager(new JsonDatabaseManager());
            boolean CourseComplete= completionManager.markLessonCompleted(
                    currentStudent,
                    currentCourse,
                    currentLesson.getLessonId(),
                    true
            );

            if(CourseComplete) {
                parentDashboard.getCertificatePanel().setStudent(currentStudent);
                parentDashboard.getCertificatePanel().reloadCertificates();
            }
        } else {
            message += "Status: FAILED. (Target: " + quiz.getPassScore() + "%)";
            if (feedback.length() > 0) {
                message += "\n\n--- Feedback ---" + feedback.toString();
            }

            int maxAttempts = quiz.getMaxAttempts();

            if (newAttemptNumber < maxAttempts) {
                int attemptsRemaining = maxAttempts - newAttemptNumber;
                message += "\n\nYou have " + attemptsRemaining + " attempts remaining.";

                mainActionButton.setText("Start Quiz for " + quiz.getPassScore() + "% (Attempts Left: " + attemptsRemaining + ")");
                mainActionButton.setActionCommand("StartQuiz");
                mainActionButton.setEnabled(true);

            } else {
                message += "\n\nMaximum attempts reached (" + maxAttempts + "). You cannot try again.";

                mainActionButton.setText("Failed - Max Attempts Reached");
                mainActionButton.setEnabled(false);
            }

            JOptionPane.showMessageDialog(this, message, "Quiz Results", JOptionPane.ERROR_MESSAGE);
        }}


    private void updateProgress() {
        List<String> completed = currentStudent.getProgressForCourse(currentCourse.getCourseId());
        int completedCount = completed.size();
        int totalCount = currentCourse.getLessons().size();
        progressLabel.setText("Progress: " + completedCount + " / " + totalCount + " completed   ");
    }
}
