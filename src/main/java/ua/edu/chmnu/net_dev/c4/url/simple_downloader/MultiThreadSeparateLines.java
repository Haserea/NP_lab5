package ua.edu.chmnu.net_dev.c4.url.simple_downloader;

import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadSeparateLines {

    // Shared console buffer to hold lines for each thread
    private static final String[] consoleBuffer = new String[10];
    private static final Object lock = new Object();  // Lock for safe access to buffer
    private static final AtomicInteger updateCount = new AtomicInteger(0);  // Track updates to refresh screen

    public static void main(String[] args) {
        // Initialize the console buffer
        for (int i = 0; i < consoleBuffer.length; i++) {
            consoleBuffer[i] = "";
        }

        // Create and start two threads that print on separate lines
        Thread thread1 = new Thread(new LineUpdater(2, "Thread 1"));  // Line 2 for Thread 1
        Thread thread2 = new Thread(new LineUpdater(4, "Thread 2"));  // Line 4 for Thread 2

        thread1.start();
        thread2.start();

        // Start a separate thread to render the buffer periodically
        new Thread(MultiThreadSeparateLines::renderBuffer).start();
    }

    // Runnable class to update specific lines in the console buffer
    static class LineUpdater implements Runnable {
        private final int lineNumber;
        private final String name;

        public LineUpdater(int lineNumber, String name) {
            this.lineNumber = lineNumber;
            this.name = name;
        }

        @Override
        public void run() {
            for (int i = 1; i <= 10; i++) {
                synchronized (lock) {
                    // Update the specific line in the buffer
                    consoleBuffer[lineNumber] = name + " - Count: " + i;
                    updateCount.incrementAndGet();  // Mark update to refresh screen
                }
                try {
                    Thread.sleep(500);  // Pause to simulate work
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Render the shared console buffer periodically
    public static void renderBuffer() {
        while (updateCount.get() < 20) {  // Render until all updates are complete
            synchronized (lock) {
                // Clear the screen and reprint the buffer
                System.out.print("\033[H\033[2J");  // Clear screen (ANSI compatible)
                for (String line : consoleBuffer) {
                    System.out.println(line);  // Print each line
                }
            }
            try {
                Thread.sleep(100);  // Refresh every 100 milliseconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
