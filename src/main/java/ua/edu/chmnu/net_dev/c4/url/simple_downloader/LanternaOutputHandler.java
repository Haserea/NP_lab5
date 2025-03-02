package ua.edu.chmnu.net_dev.c4.url.simple_downloader;

import com.googlecode.lanterna.terminal.Terminal;
import lombok.AllArgsConstructor;
import ua.edu.chmnu.net_dev.c4.url.core.OutputHandler;

import java.io.IOException;

@AllArgsConstructor
public class LanternaOutputHandler implements OutputHandler {

    private final Terminal terminal;

    @Override
    public synchronized void out(int lineNumber, String data) {
        try {
            terminal.setCursorPosition(0, lineNumber);
            terminal.putString(data);
            terminal.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
