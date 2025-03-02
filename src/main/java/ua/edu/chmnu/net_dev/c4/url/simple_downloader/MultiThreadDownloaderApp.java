package ua.edu.chmnu.net_dev.c4.url.simple_downloader;

import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import ua.edu.chmnu.net_dev.c4.url.core.OutputHandler;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Test download files can be found <a href="https://ash-speed.hetzner.com">here</a>
 */
public class MultiThreadDownloaderApp {
    public static void main(String[] args) throws IOException {
//        var sourceUrl = "https://raw.githubusercontent.com/mguludag/book-1/refs/heads/master/%5BJAVA%5D%5BJava%20Network%20Programming%2C%204th%20Edition%5D.pdf";

        String[] sources = {
                "https://ash-speed.hetzner.com/100MB.bin",
                "https://ash-speed.hetzner.com/1GB.bin"
        };

        Terminal terminal = new DefaultTerminalFactory().createTerminal();

        OutputHandler outputHandler = new LanternaOutputHandlerFactory(terminal).create();

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < sources.length; i++) {
            executorService.submit(
                    new DownloaderItem(sources[i], ".", new ConsoleProgressHandler(i + 1, outputHandler))
            );
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (terminal != null) {
                try {
                    terminal.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            executorService.shutdownNow();
        }));

        executorService.shutdown();
    }
}
