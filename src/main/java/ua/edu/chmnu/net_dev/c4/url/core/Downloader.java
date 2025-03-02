package ua.edu.chmnu.net_dev.c4.url.core;

public interface Downloader {

    String getSourceUrl();

    String getTargetPath();

    void download();
}
