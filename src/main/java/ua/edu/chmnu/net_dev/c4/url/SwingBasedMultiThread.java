package ua.edu.chmnu.net_dev.c4.url;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SwingBasedMultiThread extends JFrame {
    private JTable downloadTable;
    private DefaultTableModel tableModel;
    private List<FileDownloaderTask> tasks;
    private JLabel statusLabel;

    public SwingBasedMultiThread() {
        setTitle("Multi-thread File Downloader");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"File URL", "Status", "Progress", "Speed"}, 0);
        downloadTable = new JTable(tableModel);
        downloadTable.setRowHeight(25);
        downloadTable.setFont(new Font("Arial", Font.PLAIN, 14));
        add(new JScrollPane(downloadTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(new Color(50, 50, 50));

        JButton addButton = new JButton("Add Download");
        JButton pauseButton = new JButton("Pause");
        JButton resumeButton = new JButton("Resume");

        styleButton(addButton);
        styleButton(pauseButton);
        styleButton(resumeButton);

        buttonPanel.add(addButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resumeButton);
        add(buttonPanel, BorderLayout.NORTH);

        statusLabel = new JLabel("Status: Ready");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(Color.WHITE);
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(30, 30, 30));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);

        tasks = new ArrayList<>();

        addButton.addActionListener(e -> {
            String fileURL = JOptionPane.showInputDialog(this, "Enter file URL:");
            if (fileURL != null && !fileURL.isEmpty()) {
                addDownload(fileURL);
            }
        });

        pauseButton.addActionListener(e -> pauseDownload());
        resumeButton.addActionListener(e -> resumeDownload());
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    private void addDownload(String fileURL) {
        FileDownloaderTask task = new FileDownloaderTask(fileURL, tableModel, tableModel.getRowCount(), statusLabel);
        tableModel.addRow(new Object[]{fileURL, "Downloading", "0%", "0 B/s"});
        tasks.add(task);
        new Thread(task).start();
    }

    private void pauseDownload() {
        for (FileDownloaderTask task : tasks) {
            task.pauseDownload();
        }
        statusLabel.setText("Status: Paused");
    }

    private void resumeDownload() {
        for (FileDownloaderTask task : tasks) {
            task.resumeDownload();
        }
        statusLabel.setText("Status: Resumed");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SwingBasedMultiThread().setVisible(true));
    }

    private static class FileDownloaderTask implements Runnable {
        private final String fileURL;
        private final int rowIndex;
        private final DefaultTableModel tableModel;
        private final JLabel statusLabel;
        private static final String SAVE_DIR = "D:\\Network programming";

        private volatile boolean isPaused = false;
        private int downloaded = 0;
        private int fileSize = 0;
        private long startTime = 0;

        public FileDownloaderTask(String fileURL, DefaultTableModel tableModel, int rowIndex, JLabel statusLabel) {
            this.fileURL = fileURL;
            this.tableModel = tableModel;
            this.rowIndex = rowIndex;
            this.statusLabel = statusLabel;

            File saveDir = new File(SAVE_DIR);
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }
        }

        @Override
        public void run() {
            try {
                String fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
                File outputFile = new File(SAVE_DIR + File.separator + fileName);

                if (outputFile.exists()) {
                    tableModel.setValueAt("Completed", rowIndex, 1);
                    return;
                }

                URL url = new URL(fileURL);
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                fileSize = httpConn.getContentLength();
                InputStream inputStream = httpConn.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(outputFile);

                byte[] buffer = new byte[4096];
                int bytesRead;
                startTime = System.currentTimeMillis();

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    while (isPaused) {
                        SwingUtilities.invokeLater(() -> tableModel.setValueAt("Paused", rowIndex, 1));
                        Thread.sleep(100);
                    }

                    outputStream.write(buffer, 0, bytesRead);
                    downloaded += bytesRead;
                    updateUI();
                }

                outputStream.close();
                inputStream.close();

                if (!isPaused) {
                    tableModel.setValueAt("Completed", rowIndex, 1);
                }
                SwingUtilities.invokeLater(() -> statusLabel.setText("Status: Completed"));
            } catch (IOException | InterruptedException e) {
                tableModel.setValueAt("Error", rowIndex, 1);
                SwingUtilities.invokeLater(() -> statusLabel.setText("Status: Error"));
            }
        }

        private void updateUI() {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime > 0 && fileSize > 0) {
                long speed = downloaded * 1000L / elapsedTime;
                int progress = (int) ((downloaded * 100.0) / fileSize);

                SwingUtilities.invokeLater(() -> {
                    tableModel.setValueAt(progress + "%", rowIndex, 2);
                    tableModel.setValueAt(formatSpeed(speed), rowIndex, 3);
                });
            }
        }

        private String formatSpeed(long speed) {
            if (speed < 1024) return speed + " B/s";
            if (speed < 1048576) return (speed / 1024) + " KB/s";
            return (speed / 1048576) + " MB/s";
        }

        public void pauseDownload() { isPaused = true; }
        public void resumeDownload() { isPaused = false; }
    }
}
