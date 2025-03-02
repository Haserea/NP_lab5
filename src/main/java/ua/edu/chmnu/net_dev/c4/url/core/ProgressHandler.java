package ua.edu.chmnu.net_dev.c4.url.core;

public interface ProgressHandler {

    void handle(String fileName, int current, long total, double speed);
}
