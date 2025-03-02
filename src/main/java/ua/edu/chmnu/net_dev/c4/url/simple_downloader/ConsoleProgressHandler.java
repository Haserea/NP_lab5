package ua.edu.chmnu.net_dev.c4.url.simple_downloader;

import lombok.AllArgsConstructor;
import ua.edu.chmnu.net_dev.c4.url.core.OutputHandler;
import ua.edu.chmnu.net_dev.c4.url.core.ProgressHandler;
import ua.edu.chmnu.net_dev.c4.utils.ByteFormatter;

@AllArgsConstructor
public class ConsoleProgressHandler implements ProgressHandler {

    private final int lineNumber;

    private OutputHandler outputHandler;

    @Override
    public void handle(String fileName, int current, long total, double speed) {
        if (total < current) {
            return;
        }

        int progress = (int) (current * 100.0 / total);

        var currentDownloaded = ByteFormatter.format(current);

        var totalDownloaded = ByteFormatter.format(total);

        var speedDownload = ByteFormatter.format((long) speed);

        String outString = String.format("%s - %s/%s [%d%%, %s/s]", fileName, currentDownloaded, totalDownloaded, progress, speedDownload);

        outputHandler.out(lineNumber, outString);
    }
}
