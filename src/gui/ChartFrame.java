package gui;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import backend.StatisticsManager;
import model.Course;
import model.Lesson;

public class ChartFrame extends JFrame {
    private StatisticsManager stats;

    public ChartFrame(Course course){
        stats = new StatisticsManager();
        setTitle("Insights - "+course.getTitle());
        setSize(900,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        JLabel titleTable=new JLabel("Insights:"+course.getTitle(),SwingConstants.CENTER);
        titleTable.setFont(new Font("Arial",Font.BOLD,25));
        add(titleTable,BorderLayout.NORTH);
        JPanel chartContainer=new JPanel();
        chartContainer.setLayout(new BoxLayout(chartContainer,BoxLayout.Y_AXIS));

        Map<String,Double> lessonAverages=new LinkedHashMap<>();
        for (Lesson lesson: course.getLessons()){
            lessonAverages.put(lesson.getTitle(),stats.getLessonAverage(course.getCourseId(), lesson.getLessonId()));
        }
        ChartPanel averagesPanel=new ChartPanel(lessonAverages,"Lesson Quiz Averages");
        averagesPanel.setPreferredSize(new Dimension(800,300));
        chartContainer.add(averagesPanel);
        chartContainer.add(Box.createRigidArea(new Dimension(0,10)));

        Map<String,Double> lessonCompletion=new LinkedHashMap<>();
        for (Lesson lesson: course.getLessons()){
            lessonCompletion.put(lesson.getTitle(),stats.getLessonCompletionRate(course.getCourseId(), lesson.getLessonId()));
        }
        ChartPanel completionPanel =new ChartPanel(lessonCompletion,"Lesson Completion Rate");
        completionPanel.setPreferredSize(new Dimension(800,300));
        chartContainer.add(completionPanel);


        JScrollPane scrollPane=new JScrollPane(chartContainer);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane,BorderLayout.CENTER);

        double courseAvg= stats.getCourseAverage(course.getCourseId());
        double courseCompletion= stats.getCourseCompletionRate(course.getCourseId(),course.getLessons().size());

        JPanel summaryPanel=new JPanel(new GridLayout(1,2,10,10));
        JLabel courseAvgLabel=new JLabel("Course Average: "+String.format("%.1f",courseAvg),SwingConstants.CENTER);
        JLabel courseCompletionLabel=new JLabel("Course Completion: "+String.format("%.1f", courseCompletion),SwingConstants.CENTER);

        courseAvgLabel.setFont(new Font("Arial",Font.BOLD,16));
        courseCompletionLabel.setFont(new Font("Arial",Font.BOLD,16));

        summaryPanel.add(courseAvgLabel);
        summaryPanel.add(courseCompletionLabel);
        add(summaryPanel,BorderLayout.SOUTH);
        setVisible(true);
    }

}