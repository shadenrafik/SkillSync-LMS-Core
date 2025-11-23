package gui;

import model.Student;
import model.Certificate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.File;
import java.io.IOException;


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
        downloadButton.addActionListener(e -> {
            if (certificateTable.getSelectedRow() != -1) {
                Certificate cert = student.getCertificates()
                        .get(certificateTable.getSelectedRow());


                String certContent = "Certificate ID: " + cert.getCertificateId() + "\n" +
                        "Course ID: " + cert.getCourseId() + "\n" +
                        "Student ID: " + student.getUserId() + "\n" +
                        "Issue Date: " + cert.getIssueDate() + "\n" +
                        "\nCongratulations on completing the course!";


                String userHome = System.getProperty("user.home");
                String downloadsPath = userHome + File.separator + "Downloads";
                String fileName = downloadsPath + File.separator + "Certificate_" + cert.getCourseId() + ".pdf";

                downloadCertificatePDF(certContent, fileName);
            } else {
                JOptionPane.showMessageDialog(this, "Select a certificate first.");
            }
        });

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
    public void downloadCertificatePDF(String certificateContent, String fileName) {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.setLeading(20f);
            contentStream.newLineAtOffset(50, 700);

            String[] lines = certificateContent.split("\n");
            for (String line : lines) {
                contentStream.showText(line);
                contentStream.newLine();
            }

            contentStream.endText();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (!fileName.toLowerCase().endsWith(".pdf")) {
                fileName += ".pdf";
            }
            document.save(new File(fileName));
            JOptionPane.showMessageDialog(this, "Certificate saved to: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void reloadCertificates() {
        loadCertificates();
    }




    public void setStudent(Student student) {
        this.student = student;
        loadCertificates();
    }
}
