package gui;

import model.Student;
import model.Certificate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.util.List;



public class CertificatePanel extends JPanel {

    private Student student;
    private JTable certificateTable;
    private JButton downloadButton;
    private JButton refreshButton;

    public CertificatePanel(Student student) {
        this.student = student;
        initComponents();
        loadCertificates();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        certificateTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(certificateTable);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        downloadButton = new JButton("Download PDF");
        refreshButton = new JButton("Refresh");


        refreshButton.addActionListener(e -> loadCertificates());

        bottomPanel.add(downloadButton);
        bottomPanel.add(refreshButton);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadCertificates() {
        List<Certificate> certs = student.getCertificates();

        String[] columns = {"Certificate ID", "Course ID", "Issue Date"};
        Object[][] data = new Object[certs.size()][3];

        for (int i = 0; i < certs.size(); i++) {
            Certificate c = certs.get(i);
            data[i][0] = c.getCertificateId();
            data[i][1] = c.getCourseId();
            data[i][2] = c.getIssueDate();
        }

        certificateTable.setModel(new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        if (!certs.isEmpty()) {
            certificateTable.setRowSelectionInterval(certs.size() - 1, certs.size() - 1);
            certificateTable.scrollRectToVisible(
                    certificateTable.getCellRect(certs.size() - 1, 0, true)
            );
        }
    }
    public void reloadCertificates() {
        loadCertificates(); // call the private method internally
    }




    public void setStudent(Student student) {
        this.student = student;
        loadCertificates();
    }
}
