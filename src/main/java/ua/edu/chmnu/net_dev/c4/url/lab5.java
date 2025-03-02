package ua.edu.chmnu.net_dev.c4.url;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class lab5 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a URL: ");
        String urlString = scanner.nextLine();

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder htmlContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    htmlContent.append(line);
                }
                reader.close();

                String html = htmlContent.toString();
                Pattern pattern = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(html);

                if (matcher.find()) {
                    System.out.println("Page Title: " + matcher.group(1).trim());
                } else {
                    System.out.println("No <title> tag found in the webpage.");
                }
            } else {
                System.out.println("Failed to retrieve the webpage. Response Code: " + responseCode);
            }
        } catch (IOException e) {
            System.out.println("Error accessing the URL: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}