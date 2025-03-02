package ua.edu.chmnu.net_dev.c4.url;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


// Single-thread downloader
public class SingleThreadDownloader {
    public static void downloadFile(String fileURL, String saveDir) {
        try {
            URL url = new URL(fileURL);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = "";
                String disposition = httpConnection.getHeaderField("Content-Disposition");
                if (disposition != null && disposition.contains("filename=")) {
                    fileName = disposition.split("filename=")[1].replaceAll("\"", "").trim();
                } else {
                    fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
                }

                InputStream inputStream = httpConnection.getInputStream();
                File saveFile = new File(saveDir + File.separator + fileName);

                FileOutputStream outputStream = new FileOutputStream(saveFile);
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();
                System.out.println("Download complete: " + saveFile.getAbsolutePath());
            } else {
                System.out.println("Server returned HTTP response code: " + responseCode);
            }
            httpConnection.disconnect();
        } catch (IOException e) {
            System.err.println("Error during file download: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the file URL: ");
        String fileURL = scanner.nextLine();
        System.out.print("Enter the save directory: ");
        String saveDir = scanner.nextLine();
        downloadFile(fileURL, saveDir);
    }
}