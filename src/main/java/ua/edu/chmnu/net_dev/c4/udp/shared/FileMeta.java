package ua.edu.chmnu.net_dev.c4.udp.shared;

import java.io.Serializable;

public record FileMeta(String fileName, long totalSize) implements Serializable {
}
