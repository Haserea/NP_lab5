package ua.edu.chmnu.net_dev.c4.url;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.*;


public class MultiThreadDownloader {
    private static final int BUFFER_SIZE = 4096;

    public static void downloadFile(String fileURL, String saveDir, int numThreads) {
        try {
            URL url = new URL(fileURL);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                int contentLength = httpConnection.getContentLength();
                if (contentLength > 1024 * 1024 * 1024) {
                    System.err.println("File size exceeds 1GB limit.");
                    return;
                }

                String fileName = "";
                String disposition = httpConnection.getHeaderField("Content-Disposition");
                if (disposition != null && disposition.contains("filename=")) {
                    fileName = disposition.split("filename=")[1].replaceAll("\"", "").trim();
                } else {
                    fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
                }

                File saveFile = new File(saveDir + File.separator + fileName);

                ExecutorService executor = Executors.newFixedThreadPool(numThreads);
                int chunkSize = contentLength / numThreads;
                int remainder = contentLength % numThreads;

                for (int i = 0; i < numThreads; i++) {
                    int startByte = i * chunkSize;
                    int endByte = (i == numThreads - 1) ? (startByte + chunkSize + remainder - 1) : (startByte + chunkSize - 1);

                    executor.execute(new DownloadTask(fileURL, saveFile, startByte, endByte, i));
                }

                executor.shutdown();
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
                System.out.println("Download complete: " + saveFile.getAbsolutePath());
            } else {
                System.out.println("Server returned HTTP response code: " + responseCode);
            }
            httpConnection.disconnect();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error during file download: " + e.getMessage());
        }
    }

    private static class DownloadTask implements Runnable {
        private final String fileURL;
        private final File saveFile;
        private final int startByte;
        private final int endByte;
        private final int threadId;

        public DownloadTask(String fileURL, File saveFile, int startByte, int endByte, int threadId) {
            this.fileURL = fileURL;
            this.saveFile = saveFile;
            this.startByte = startByte;
            this.endByte = endByte;
            this.threadId = threadId;
        }

        @Override
        public void run() {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(fileURL).openConnection();
                String byteRange = "bytes=" + startByte + "-" + endByte;
                connection.setRequestProperty("Range", byteRange);

                InputStream inputStream = connection.getInputStream();
                RandomAccessFile raf = new RandomAccessFile(saveFile, "rw");
                raf.seek(startByte);

                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    raf.write(buffer, 0, bytesRead);
                }

                raf.close();
                inputStream.close();
                System.out.println("Thread " + threadId + " finished downloading range: " + byteRange);
            } catch (IOException e) {
                System.err.println("Thread " + threadId + " encountered an error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the file URL: ");
        String fileURL = scanner.nextLine();
        System.out.print("Enter the save directory: ");
        String saveDir = scanner.nextLine();
        System.out.print("Enter the number of threads: ");
        int numThreads = scanner.nextInt();
        downloadFile(fileURL, saveDir, numThreads);
    }
}
