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

    public SwingBasedMultiThread() {
        setTitle("Swing multi-thread file downloader");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"File URL", "Status", "Progress", "Speed"}, 0);
        downloadTable = new JTable(tableModel);
        add(new JScrollPane(downloadTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add download");
        JButton pauseButton = new JButton("Pause");
        JButton resumeButton = new JButton("Resume");

        buttonPanel.add(addButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resumeButton);
        add(buttonPanel, BorderLayout.NORTH);

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

    private void addDownload(String fileURL) {
        FileDownloaderTask task = new FileDownloaderTask(fileURL, tableModel, tableModel.getRowCount());
        tableModel.addRow(new Object[]{fileURL, "Downloading", 0, "0 B/s"});
        tasks.add(task);
        new Thread(task).start();
    }

    private void pauseDownload() {
        for (FileDownloaderTask task : tasks) {
            task.pauseDownload();
        }
    }

    private void resumeDownload() {
        for (FileDownloaderTask task : tasks) {
            task.resumeDownload();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SwingBasedMultiThread().setVisible(true));
    }

    // FileDownloaderTask class for downloading files
    private static class FileDownloaderTask implements Runnable {
        private final String fileURL;
        private final int rowIndex;
        private final DefaultTableModel tableModel;
        private static final String SAVE_DIR = "C:\\Users\\Shado\\Downloads\\lab5.2";

        private volatile boolean isPaused = false;
        private volatile boolean isStopped = false;

        private int downloaded = 0;
        private int fileSize = 0;
        private long startTime = 0;

        public FileDownloaderTask(String fileURL, DefaultTableModel tableModel, int rowIndex) {
            this.fileURL = fileURL;
            this.tableModel = tableModel;
            this.rowIndex = rowIndex;

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
            } catch (IOException | InterruptedException e) {
                tableModel.setValueAt("Error", rowIndex, 1);
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
            if (speed < 1024) {
                return speed + " B/s";
            } else if (speed < 1048576) {
                return (speed / 1024) + " KB/s";
            } else {
                return (speed / 1048576) + " MB/s";
            }
        }

        public void pauseDownload() {
            isPaused = true;
        }

        public void resumeDownload() {
            isPaused = false;
            SwingUtilities.invokeLater(() -> tableModel.setValueAt("Downloading", rowIndex, 1));
        }
    }
}
