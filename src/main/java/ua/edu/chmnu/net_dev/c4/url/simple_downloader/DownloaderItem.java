package ua.edu.chmnu.net_dev.c4.url.simple_downloader;

import lombok.AllArgsConstructor;
import lombok.Data;
import ua.edu.chmnu.net_dev.c4.url.core.Downloader;
import ua.edu.chmnu.net_dev.c4.url.core.ProgressHandler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

@Data
@AllArgsConstructor
public class DownloaderItem implements Downloader, Runnable {

    private final static int BUFFER_SIZE = 1024 * 1024;

    private final String sourceUrl;

    private final String targetPath;

    private final ProgressHandler progressHandler;

    private final int bufferSize;

    public DownloaderItem(String sourceUrl, String targetPath) {
        this(sourceUrl, targetPath, null, BUFFER_SIZE);
    }

    public DownloaderItem(String sourceUrl, String targetPath, ProgressHandler progressHandler) {
        this(sourceUrl, targetPath, progressHandler, BUFFER_SIZE);
    }

    @Override
    public void run() {
        download();
    }

    @Override
    public void download() {
        try {
            URL url = new URL(sourceUrl);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            var sourcePath = URLDecoder.decode(url.getPath(), "UTF-8");

            var totalBytes = urlConnection.getContentLengthLong();

            fetchFile(urlConnection.getInputStream(), totalBytes, getFileName(sourcePath), this.targetPath);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFileName(String path) {
        var idx = path.lastIndexOf('/');
        return idx == -1 ? path : path.substring(idx + 1);
    }

    private void fetchFile(InputStream is, long totalBytes, String fileName, String targetPath) throws IOException {
        byte[] buffer = new byte[bufferSize];

        int currentRead = 0;

        if (!new File(targetPath).exists()) {
            new File(targetPath).mkdirs();
        }

        targetPath = targetPath + File.separator + fileName;

        try (var bis = new BufferedInputStream(is);
             var os = new FileOutputStream(targetPath)) {

            int read;

            var startTime = System.nanoTime();

            var lastCheckTime = startTime;

            while ((read = bis.read(buffer)) != -1) {

                var currentTime = System.nanoTime();

                var elapsedTimeSinceLastCheck = currentTime - lastCheckTime;

                currentRead += read;

                os.write(buffer, 0, read);

                if (elapsedTimeSinceLastCheck >= 1_000_000_000L) {
                    lastCheckTime = currentTime;
                }

                var speed = read * 1_000_000_000.0 / elapsedTimeSinceLastCheck;

                if (progressHandler != null) {
                    progressHandler.handle(fileName, currentRead, totalBytes, speed);
                }
            }

            var endTime = System.nanoTime();

            var totalTime = endTime - startTime;

            var avgSpeed = currentRead * 1_000_000_000.0 / totalTime;

            if (progressHandler != null) {
                progressHandler.handle(fileName, currentRead, totalBytes, avgSpeed);
            }
        }
    }
}
