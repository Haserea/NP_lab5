package ua.edu.chmnu.net_dev.c4.url.simple_downloader;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.Terminal;
import lombok.AllArgsConstructor;
import ua.edu.chmnu.net_dev.c4.url.core.OutputHandler;
import ua.edu.chmnu.net_dev.c4.url.core.OutputHandlerFactory;

import java.io.IOException;

@AllArgsConstructor
public class LanternaOutputHandlerFactory implements OutputHandlerFactory {

    private final Terminal terminal;

    @Override
    public OutputHandler create() {
        try {
            terminal.setCursorPosition(0, 0);
            terminal.setBackgroundColor(TextColor.ANSI.DEFAULT);
            terminal.setForegroundColor(TextColor.ANSI.WHITE);
            terminal.clearScreen();

            return new LanternaOutputHandler(terminal);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
